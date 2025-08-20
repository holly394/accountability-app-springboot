package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.user.UserService;
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

@Controller
@RequestMapping("/api/relationships")
@ResponseBody
public class RelationshipController {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public List<RelationshipData> search(@AuthenticationPrincipal AccountabilitySessionUser user,
                                         @RequestParam String username) {
        List<User> searchList = userRepository.findUsersByUsernameContainsIgnoreCase(username);
        User thisUser = userRepository.findUserById(user.getId());
        if (!Objects.equals(username, "")) { // an empty spring, caused by backspace
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

        return relationshipRepository.getRelationshipsByUserIdAndStatusIgnoreDirection(user.getId(), status, pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //
    //FIXME: Do not use separate URLs/methods for something that can be sent parameterized by the frontend
    //

    //list of pending requests the current user has RECEIVED
    @GetMapping("/pending-requests-to-answer")
    public Page<RelationshipData> getPendingRequestsToAnswer(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                             @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRelationshipsByUserIdAndStatus(user.getId(), List.of(RelationshipStatus.PENDING), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of pending requests where the current user has to WAIT for an answer
    @GetMapping("/pending-requests-to-wait")
    public Page<RelationshipData> getPendingRequestsToWait(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                           @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRelationshipsByPartnerIdAndStatus(user.getId(), List.of(RelationshipStatus.PENDING), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of rejected requests where the current user gave the rejection
    @GetMapping("/rejected-requests-sent")
    public Page<RelationshipData> getRejectionsSent(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRelationshipsByUserIdAndStatus(user.getId(), List.of(RelationshipStatus.REJECTED), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }

    //list of rejected requests where the current user received the rejection
    @GetMapping("/rejected-requests-received")
    public Page<RelationshipData> getRejectionsReceived(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return relationshipRepository.getRelationshipsByPartnerIdAndStatus(user.getId(), List.of(RelationshipStatus.REJECTED), pageable)
                .map(this::convertRelationshipToRelationshipData);
    }



    @PutMapping("/request/{partnerId}")
    public List<RelationshipData> sendRequest(@AuthenticationPrincipal AccountabilitySessionUser user,
                                              @PathVariable Long partnerId) {
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
        Relationship existingRelationship = relationshipRepository.findRelationship(thisUser.getId(), partner.getId());
        if (existingRelationship != null) {
            //WHY: because REQUESTED stays the same in one direction even if updated
            //need to find status of relationship in other direction to see if PENDING, APPROVED, or REJECTED
            if(existingRelationship.getStatus() == RelationshipStatus.REQUESTED) {
                existingRelationship = relationshipRepository.findRelationship(partner.getId(), thisUser.getId());
            }
            return convertRelationshipToRelationshipData(existingRelationship);
        }

        Relationship relationship = new Relationship();
        relationship.setUser(thisUser);
        relationship.setPartner(partner);
        return convertRelationshipToRelationshipData(relationship);
    }

    private RelationshipData convertRelationshipToRelationshipData(Relationship relationship) {
        return new RelationshipData(
                relationship.getId(),
                relationship.getStatus(),
                userService.convertUserToDto(relationship.getUser()),
                userService.convertUserToDto(relationship.getPartner()));
    }

    /**
     * Since this data is used to display to the current user, the relationship gets flipped to where each relationship is seen from the perspective of the logged in user.
     */
    private RelationshipData convertToRelationshipDataWhereCurrentUserIsNeverPartner(Long currentUserId, RelationshipData relationshipData){
        if (Objects.equals(relationshipData.getPartner().getId(), currentUserId)) {
            return relationshipData.flipped();
        }
        return relationshipData;
    }


}
