package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/api/relationships")
@ResponseBody
public class RelationshipController {
    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public List<RelationshipStatusDto> getAllRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        List<Relationship> existingRelationships = relationshipRepository.getAllByUserId(user.getId());
        return existingRelationships.stream()
                .map(result -> convertPartnertoRelationshipStatusDto(user.getId(), result.getPartner().getId()))
                .collect(toList());
    }

    @GetMapping("/search")
    public List<RelationshipStatusDto> search(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam String username) {
        List<User> partnerList = userRepository.findUsersByUsernameContainsIgnoreCase(username);
        return partnerList.stream()
                .filter(thisPartner -> !thisPartner.getUsername().equals(user.getUsername()))
                .map(partner -> convertPartnertoRelationshipStatusDto(user.getId(), partner.getId()))
                .toList();
    }

    @GetMapping("/approved-list")
    public List<Relationship> getApprovedRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        return relationshipRepository.getApprovedByUserId(user.getId());
    }

    @GetMapping("/pending-request-list")
    public List<Relationship> getPendingRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        return relationshipRepository.getPendingByUserId(user.getId());
    }

    @GetMapping("/send-request")
    public Relationship addRelationship(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam Long partnerId) {
        if(relationshipRepository.findRelationship(user.getId(), partnerId) == null) {
            throw new IllegalArgumentException("Relationship already exists");
        }

        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);

        Relationship newRelationshipRequest = new Relationship(thisUser, partner, RelationshipStatus.REQUESTED);
        Relationship newRelationshipPending = new Relationship(partner, thisUser, RelationshipStatus.PENDING);

        relationshipRepository.save(newRelationshipPending);
        return relationshipRepository.save(newRelationshipRequest);
    }

    @GetMapping("/approve-request")
    public Relationship approveRelationship(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestParam Long partnerId) {
        if(relationshipRepository.findRelationship(user.getId(), partnerId) == null) {
            throw new IllegalArgumentException("Relationship does not exist");
        }
        Relationship newRelationshipApproved = relationshipRepository.findRelationship(user.getId(), partnerId);

        if(newRelationshipApproved.getStatus() != RelationshipStatus.PENDING) {
            throw new IllegalArgumentException("You cannot approve this relationship");
        }

        newRelationshipApproved.setStatus(RelationshipStatus.APPROVED);
        return relationshipRepository.save(newRelationshipApproved);
    }

    //delete request
    @DeleteMapping("/delete-request")
    public ResponseEntity<Void> deleteRelationship(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody Long relationshipId) {
        if(!relationshipRepository.existsById(relationshipId)) {
            throw new IllegalArgumentException("Relationship does not exist");
        }

        //Optional.get() without isPresent() check
        Relationship relationshipToDelete = relationshipRepository.findById(relationshipId).get();
        Relationship otherDirection = getOppositeDirection(relationshipToDelete);

        //null pointer exception (fix)
        if(relationshipToDelete.getUser().getId().equals(user.getId()) || relationshipToDelete.getPartner().getId().equals(user.getId())) {
            relationshipRepository.delete(relationshipToDelete);
            relationshipRepository.delete(otherDirection);
        }

        return ResponseEntity.noContent().build();
    }


    private RelationshipStatusDto convertPartnertoRelationshipStatusDto(Long thisUserId, Long partnerId) {
        RelationshipStatusDto relationshipStatusDto = new RelationshipStatusDto();
        User partner = userRepository.findUserById(partnerId);

        if(relationshipRepository.findRelationship(thisUserId, partnerId) == null){
            assert partner != null;
            relationshipStatusDto.setPartnerUsername(partner.getUsername());
            relationshipStatusDto.setPartnerId(partner.getId());
            return relationshipStatusDto;
        }

        Relationship relationship = relationshipRepository.findRelationship(thisUserId, partnerId);
        assert partner != null;
        relationshipStatusDto.setPartnerUsername(partner.getUsername());
        relationshipStatusDto.setPartnerId(partner.getId());

        if(relationship.getStatus() == RelationshipStatus.REQUESTED) {
            relationship = getOppositeDirection(relationship);
        }

        relationshipStatusDto.setId(relationship.getId());
        relationshipStatusDto.setStatus(relationship.getStatus());
        return relationshipStatusDto;
    }

    private Relationship getOppositeDirection(Relationship oneDirection) {
        Long userId = oneDirection.getUser().getId();
        Long partnerId = oneDirection.getPartner().getId();
        return relationshipRepository.findRelationship(partnerId, userId);
    }

}
