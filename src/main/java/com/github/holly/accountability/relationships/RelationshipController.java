package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/this-user")
    public UserDto getUser(@AuthenticationPrincipal AccountabilitySessionUser user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setId(user.getId());
        return userDto;
    }

    @GetMapping("")
    public List<RelationshipData> getAllRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        return relationshipRepository.getAllByUserId(user.getId()).stream()
                .map(this::convertRelationshipToRelationshipData)
                .toList();
    }

    @GetMapping("/search")
    public List<RelationshipData> search(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam String username) {
        List<User> partnerList = userRepository.findUsersByUsernameContainsIgnoreCase(username);
        User thisUser = userRepository.findUserById(user.getId());
        return partnerList.stream()
                .filter(thisPartner -> !thisPartner.getUsername().equals(user.getUsername()))
                .map(partner -> {
                    assert thisUser != null;
                    return findOrMakeRelationshipData(thisUser, partner);
                })
                .toList();
    }

    @PostMapping("/send-request")
    public void sendRequest(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam Long partnerId) {
        if(relationshipRepository.findRelationship(user.getId(), partnerId) != null) {
            throw new IllegalArgumentException("Relationship already exists");
        }

        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);

        Relationship newRelationshipRequest = new Relationship(thisUser, partner, RelationshipStatus.REQUESTED);
        Relationship newRelationshipPending = new Relationship(partner, thisUser, RelationshipStatus.PENDING);

        relationshipRepository.save(newRelationshipPending);
        relationshipRepository.save(newRelationshipRequest);
    }

    //delete request
    @PostMapping("/delete-request")
    public void deleteRequest(@RequestParam Long relationshipId) {
        Relationship relationshipToDelete = relationshipRepository.getById(relationshipId);
        Relationship relationshipOtherDirection =
                relationshipRepository.findRelationship(relationshipToDelete.getPartner().getId(), relationshipToDelete.getUser().getId());

        relationshipRepository.delete(relationshipToDelete);
        relationshipRepository.delete(relationshipOtherDirection);
    }

    @GetMapping("/approve-request")
    public RelationshipData approveRequest(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam Long relationshipId) {
        Relationship relationshipToApprove = relationshipRepository.getById(relationshipId);

        if(!Objects.equals(relationshipToApprove.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You cannot approve this relationship");
        }

        if(relationshipToApprove.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalArgumentException("You cannot approve this relationship");
        }

        relationshipToApprove.setStatus(RelationshipStatus.APPROVED);
        relationshipRepository.save(relationshipToApprove);
        return convertRelationshipToRelationshipData(relationshipToApprove);
    }

    @GetMapping("/reject-request")
    public RelationshipData rejectRequest(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam Long relationshipId) {
        Relationship relationshipToReject = relationshipRepository.getById(relationshipId);

        if(!Objects.equals(relationshipToReject.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You cannot reject this relationship");
        }

        if(relationshipToReject.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalArgumentException("You cannot reject this relationship");
        }

        relationshipToReject.setStatus(RelationshipStatus.REJECTED);
        relationshipRepository.save(relationshipToReject);
        return convertRelationshipToRelationshipData(relationshipToReject);
    }

    private RelationshipData findOrMakeRelationshipData(User thisUser, User partner) {
        if(relationshipRepository.findRelationship(thisUser.getId(), partner.getId()) != null){
            Relationship existing = relationshipRepository.findRelationship(thisUser.getId(), partner.getId());
            return convertRelationshipToRelationshipData(existing);
        }

        Relationship relationship = new Relationship();
        relationship.setUser(thisUser);
        relationship.setPartner(partner);
        return convertRelationshipToRelationshipData(relationship);
    }

    private RelationshipData convertRelationshipToRelationshipData(Relationship relationship) {
        RelationshipData relationshipData = new RelationshipData();

        relationshipData.setUserId(relationship.getUser().getId());
        relationshipData.setPartnerId(relationship.getPartner().getId());
        relationshipData.setUserName(relationship.getUser().getUsername());
        relationshipData.setPartnerName(relationship.getPartner().getName());
        relationshipData.setId(relationship.getId());
        relationshipData.setStatus(relationship.getStatus());

        return relationshipData;
    }




}
