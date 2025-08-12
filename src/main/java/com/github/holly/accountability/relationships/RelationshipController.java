package com.github.holly.accountability.relationships;

import com.github.holly.accountability.tasks.TaskData;
import com.github.holly.accountability.tasks.TaskRepository;
import com.github.holly.accountability.tasks.TaskService;
import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.users.UserDto;
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
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private TaskService taskService;

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

    @GetMapping("/get-approved-partner-id-list")
    public List<Long> getPartners(@AuthenticationPrincipal AccountabilitySessionUser user) {
        List<Relationship> relationships = relationshipRepository.getApprovedRelationshipsByUserIdBothDirections(user.getId());
        List<User> partners = relationshipService.getCleanPartnerList(relationships, user.getId());
        return partners.stream().map(User::getId).toList();
    }

    @GetMapping("/get-partner-tasks")
    public Page<TaskData> getPartnerTasks(@AuthenticationPrincipal AccountabilitySessionUser user, @PageableDefault(size = 20) Pageable pageable) {
        List<Relationship> relationships = relationshipRepository.getApprovedRelationshipsByUserIdBothDirections(user.getId());
        List<User> partners = relationshipService.getCleanPartnerList(relationships, user.getId());
        List<Long> partnerIds = partners.stream().map(User::getId).toList();
        return taskRepository.findByUserIdIn(partnerIds, pageable)
                .map(taskService::convertTaskToDto);
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
    public List<Long> deleteRequest(@PathVariable Long relationshipId) {
        Relationship relationshipToDelete = relationshipRepository.getById(relationshipId);
        Relationship relationshipOtherDirection =
                relationshipRepository.findRelationship(relationshipToDelete.getPartner().getId(), relationshipToDelete.getUser().getId());

        List<Long> idsToDelete = new ArrayList<>();
        idsToDelete.add(relationshipToDelete.getId());
        idsToDelete.add(relationshipOtherDirection.getId());

        relationshipRepository.delete(relationshipToDelete);
        relationshipRepository.delete(relationshipOtherDirection);
        return idsToDelete;
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
