package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Backend controller to handle API calls from frontend for our relationship-related data

//Repositories: UserRepository, RelationshipRepository
//Services: UserService, RelationshipService
//DTOs: UserDto, RelationshipData

@Controller
@RequestMapping("/api/relationships")
@ResponseBody
public class RelationshipController {
    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public List<RelationshipData> search(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam String username) {
        List<User> searchList = userRepository.findUsersByUsernameContainsIgnoreCase(username);
        User thisUser = userRepository.findUserById(user.getId());
        if(!Objects.equals(username, "")){ //WHY: when pressing backspace in search bar, all users were listed before
            return searchList.stream()
                    .filter(thisPartner -> !thisPartner.getUsername().equals(user.getUsername()))
                    .map(partner -> findOrMakeRelationshipData(thisUser, partner))
                    .map(relationship -> convertToRelationshipDataWhereCurrentUserIsNeverPartner(user.getId(), relationship))
                    .toList();
        }
        return List.of(); //return empty list
    }

    @GetMapping("") //can't use when direction of request is important (mainly PENDING & REJECTED)
    public Page<RelationshipData> getRelationshipsByStatus(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                      @RequestParam(defaultValue = "REQUESTED, APPROVED, REJECTED, PENDING") List<RelationshipStatus> status,
                                                      @PageableDefault(size = 20) Pageable pageable) {

        return relationshipRepository.getRelationshipsByUserIdAndStatus(user.getId(), status, pageable)
                .map(this::convertRelationshipToRelationshipData);
        //if pageable is input, can only map with functions that don't need another param
    }

    //list of pending requests the current user has RECEIVED
    @GetMapping("/pending-requests-to-answer")
    public Page<RelationshipData> getPendingRequestsToAnswer(@AuthenticationPrincipal AccountabilitySessionUser user, @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getToAnswerRequests(user.getId(), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of pending requests where the current user has to WAIT for an answer
    @GetMapping("/pending-requests-to-wait")
    public Page<RelationshipData> getPendingRequestsToWait(@AuthenticationPrincipal AccountabilitySessionUser user, @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getUnansweredRequests(user.getId(), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of rejected requests where the current user gave the rejection
    @GetMapping("/rejected-requests-sent")
    public Page<RelationshipData> getRejectionsSent(@AuthenticationPrincipal AccountabilitySessionUser user, @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRejectionsSent(user.getId(), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of rejected requests where the current user received the rejection
    @GetMapping("/rejected-requests-received")
    public Page<RelationshipData> getRejectionsReceived(@AuthenticationPrincipal AccountabilitySessionUser user, @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRejectionsReceived(user.getId(), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }



    @PutMapping("/request/{partnerId}")
    public List<RelationshipData> sendRequest(@AuthenticationPrincipal AccountabilitySessionUser user, @PathVariable Long partnerId) {
        if(relationshipRepository.findRelationship(user.getId(), partnerId) != null) {
            throw new IllegalArgumentException("Relationship already exists");
        }

        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);

        Relationship newRelationshipRequest = new Relationship(thisUser, partner, RelationshipStatus.REQUESTED);
        Relationship newRelationshipPending = new Relationship(partner, thisUser, RelationshipStatus.PENDING);

        relationshipRepository.save(newRelationshipPending);
        relationshipRepository.save(newRelationshipRequest);

        List<RelationshipData> relationshipsToAdd = new ArrayList<>();
        relationshipsToAdd.add(convertRelationshipToRelationshipData(newRelationshipRequest));
        relationshipsToAdd.add(convertRelationshipToRelationshipData(newRelationshipPending));
        return relationshipsToAdd;
    }

    //delete request
    @DeleteMapping("/{relationshipId}")
    public void deleteRequest(@PathVariable Long relationshipId) {
        Relationship relationshipToDelete = relationshipRepository.getById(relationshipId);
        Relationship relationshipOtherDirection =
                relationshipRepository
                .findRelationship(relationshipToDelete.getPartner().getId(),
                        relationshipToDelete.getUser().getId());

        relationshipRepository.delete(relationshipToDelete);
        relationshipRepository.delete(relationshipOtherDirection);
    }

    @PostMapping("/{relationshipId}")
    public RelationshipData answerRequest(@AuthenticationPrincipal AccountabilitySessionUser user,
                                          @PathVariable Long relationshipId,
                                          @RequestBody RelationshipStatusDto status) {
        Relationship relationship = relationshipRepository.getById(relationshipId);
        RelationshipStatus newStatus = status.getStatus();

        if(relationship.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalArgumentException("You cannot approve this relationship");
        }

        if(!Objects.equals(relationship.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You cannot change this relationship");
        }

        relationship.setStatus(newStatus);

        relationshipRepository.save(relationship);
        return convertRelationshipToRelationshipData(relationship);
    }

    //either finds whether there is an existing relationship between the two users
    //or creates a new relationship where the STATUS is NULL
    private RelationshipData findOrMakeRelationshipData(User thisUser, User partner) {
        Relationship checkIfExisting = relationshipRepository.findRelationship(thisUser.getId(), partner.getId());
        if(checkIfExisting != null){
            //WHY: because REQUESTED stays the same in one direction even if updated
            //need to find status of relationship in other direction to see if PENDING, APPROVED, or REJECTED
            if(checkIfExisting.getStatus() == RelationshipStatus.REQUESTED) {
                checkIfExisting = relationshipRepository.findRelationship(partner.getId(), thisUser.getId());
            }
            return convertRelationshipToRelationshipData(checkIfExisting);
        }

        Relationship relationship = new Relationship();
        relationship.setUser(thisUser);
        relationship.setPartner(partner);
        return convertRelationshipToRelationshipData(relationship);
    }

    //takes Relationship and converts to RelationshipData AS IS
    private RelationshipData convertRelationshipToRelationshipData(Relationship relationship) {
        RelationshipData relationshipData = new RelationshipData();

        relationshipData.setUserId(relationship.getUser().getId());
        relationshipData.setUserName(relationship.getUser().getUsername());
        relationshipData.setPartnerId(relationship.getPartner().getId());
        relationshipData.setPartnerName(relationship.getPartner().getName());
        relationshipData.setId(relationship.getId());
        relationshipData.setStatus(relationship.getStatus());

        return relationshipData;
    }

    //WHY: FOR THE 2 PRIVATE FUNCTIONS BELOW
    //for lists, if there is a list of relationships where the current user
    //switches between being the User and Partner in the relationship
    //it would make it difficult to sort through partners.
    //so here, IF the partner ID of a relationship object equals the current user's ID that we give as a param
    //we will make it so that our current user will ALWAYS be set as the User in the relationship that is returned
    //HOWEVER, we keep relationship ID and STATUS info the same
    // so we can reference the database for a specific relationship with the correct ID

    //RELATIONSHIPDATA -> RELATIONSHIPDATA
    private RelationshipData convertToRelationshipDataWhereCurrentUserIsNeverPartner(Long currentUserId, RelationshipData relationshipData){
        RelationshipData newRelationshipData = new RelationshipData();

        if(Objects.equals(relationshipData.getPartnerId(), currentUserId)) {
            newRelationshipData.setUserId(relationshipData.getPartnerId());
            newRelationshipData.setUserName(relationshipData.getPartnerName());
            newRelationshipData.setPartnerId(relationshipData.getUserId());
            newRelationshipData.setPartnerName(relationshipData.getUserName());
            newRelationshipData.setId(relationshipData.getId());
            newRelationshipData.setStatus(relationshipData.getStatus());
            return newRelationshipData;
        }
        return relationshipData;
    }


}
