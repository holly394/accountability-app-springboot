package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserDto;
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

@Controller
@RequestMapping("/api/relationships")
@ResponseBody
public class RelationshipController {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipService relationshipService;

    @GetMapping("/search")
    public List<RelationshipDto> search(@AuthenticationPrincipal AccountabilitySessionUser user,
                                        @RequestParam String username) {

        List<User> searchList = userRepository.findUsersByUsernameContainsIgnoreCase(username);

        User thisUser = userRepository.findUserById(user.getId());

        if (!Objects.equals(username, "")) { // an empty spring, caused by backspace
            return searchList.stream()
                    .filter(thisPartner ->
                            !thisPartner.getUsername().equals(user.getUsername()))
                    .map(partner ->
                            findOrMakeRelationshipData(thisUser, partner))
                    .map(relationship ->
                            relationshipService
                                    .convertToRelationshipDataWhereGivenUserIsNeverPartner(
                                            user.getId(), relationship))
                    .toList();
        }

        return List.of(); //return empty list
    }

    @GetMapping("/get-partner-id-list")
    public List<Long> getPartnerIdList(@AuthenticationPrincipal AccountabilitySessionUser user,
                                       @PageableDefault(size = 20) Pageable pageable) {

        Page<Relationship> relationships = relationshipRepository
                .getRelationshipsByUserIdAndStatusIgnoreDirection(user.getId(),
                        List.of(RelationshipStatus.APPROVED), pageable);

        List<UserDto> users = relationshipService
                .getPartnersFromGivenListForGivenUser(user.getId(), relationships.getContent());

        return users.stream().map(UserDto::getId).toList();
    }

    @GetMapping("")
    public Page<RelationshipDto> getRelationshipsByStatus(@AuthenticationPrincipal AccountabilitySessionUser user,
                                                          @RequestParam
                                                                  (defaultValue =
                                                                  "REQUESTED, APPROVED, REJECTED, PENDING")
                                                                  List<RelationshipStatus> statuses,
                                                          @RequestParam(defaultValue =
                                                                  "SENDER, RECEIVER")
                                                                  List<RelationshipDirection> directions,
                                                          @PageableDefault(size = 20) Pageable pageable) {

        //current user is in the USER column of relationship
        if (directions.equals(List.of(RelationshipDirection.SENDER))) {

            return relationshipRepository
                    .getRelationshipsByUserIdAndStatus(
                            user.getId(), statuses, pageable)
                    .map(res -> relationshipService
                            .convertRelationshipToRelationshipData(res));
            //pending: requests the user has to answer
            //reject: rejections sent by the user
        }

        //current user is in the PARTNER column of relationship
        if (directions.equals(List.of(RelationshipDirection.RECEIVER))) {

            return relationshipRepository
                    .getRelationshipsByPartnerIdAndStatus(
                            user.getId(), statuses, pageable)
                    .map(res -> relationshipService
                            .convertRelationshipToRelationshipData(res));
            //pending: requests where the user has to wait for a response
            //reject: rejections received by the user from others
        }

        if (statuses.equals(List.of(RelationshipStatus.APPROVED))) {

            return relationshipRepository
                    //approval for relationships can happen from both the user and partner side
                    .getRelationshipsByUserIdAndStatusIgnoreDirection(
                            user.getId(), statuses, pageable)
                    .map(res ->
                            relationshipService
                                    .convertToRelationshipDataWhereGivenUserIsNeverPartner(
                                            user.getId(), res));
        }

        return relationshipRepository
                .getRelationshipsByUserIdAndStatusIgnoreDirection(
                        user.getId(), statuses, pageable)
                .map(res ->
                        relationshipService
                                .convertRelationshipToRelationshipData(res));

    }

    @PutMapping("/request/{partnerId}")
    public List<RelationshipDto> sendRequest(@AuthenticationPrincipal AccountabilitySessionUser user,
                                             @PathVariable Long partnerId) {

        if (relationshipRepository
                .findRelationship(user.getId(), partnerId) != null) {

            throw new IllegalArgumentException("Relationship already exists");
        }

        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);

        Relationship newRelationshipRequest = new Relationship(thisUser, partner, RelationshipStatus.REQUESTED);
        Relationship newRelationshipPending = new Relationship(partner, thisUser, RelationshipStatus.PENDING);

        relationshipRepository.save(newRelationshipPending);
        relationshipRepository.save(newRelationshipRequest);

        List<RelationshipDto> relationshipsToAdd = new ArrayList<>();

        relationshipsToAdd.add(relationshipService.convertRelationshipToRelationshipData(newRelationshipRequest));
        relationshipsToAdd.add(relationshipService.convertRelationshipToRelationshipData(newRelationshipPending));

        return relationshipsToAdd;
    }

    //delete request
    @DeleteMapping("/{relationshipId}")
    public void deleteRequest(@PathVariable Long relationshipId) {

        Relationship relationshipToDelete =
                relationshipRepository.getById(relationshipId);

        Relationship relationshipOtherDirection =
                relationshipRepository
                .findRelationship(relationshipToDelete.getPartner().getId(),
                        relationshipToDelete.getUser().getId());

        relationshipRepository.delete(relationshipToDelete);
        relationshipRepository.delete(relationshipOtherDirection);
    }

    @PostMapping("/{relationshipId}")
    public RelationshipDto answerRequest(@AuthenticationPrincipal AccountabilitySessionUser user,
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

        return relationshipService.convertRelationshipToRelationshipData(relationship);
    }

    //either finds whether there is an existing relationship between the two users
    //or creates a new relationship where the STATUS is NULL
    private RelationshipDto findOrMakeRelationshipData(User thisUser, User partner) {

        Relationship existingRelationship = relationshipRepository
                .findRelationship(thisUser.getId(), partner.getId());

        if (existingRelationship != null) {
            //WHY: because REQUESTED stays the same in one direction even if updated
            //need to find status of relationship in other direction to see if PENDING, APPROVED, or REJECTED
            if(existingRelationship.getStatus() == RelationshipStatus.REQUESTED) {

                existingRelationship = relationshipRepository
                        .findRelationship(partner.getId(), thisUser.getId());
            }

            return relationshipService.convertRelationshipToRelationshipData(existingRelationship);
        }

        Relationship relationship = new Relationship();
        relationship.setUser(thisUser);
        relationship.setPartner(partner);

        return relationshipService.convertRelationshipToRelationshipData(relationship);
    }

}
