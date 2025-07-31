package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.AccountabilitySessionUser;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserRepository;
import com.github.holly.accountability.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/search")
    public List<UserDto> search(@RequestParam String username) {
        List<User> userList = userRepository.findUsersByUsernameContainsIgnoreCase(username);
        return userList.stream().map(this::convertUsertoUserDto).collect(toList());
    }

    @GetMapping("/approved-list")
    public List<Relationship> getApprovedRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        return relationshipRepository.getByUserIdAndStatus(user.getId(), RelationshipStatus.APPROVED);
    }

    @GetMapping("/pending-requests")
    public List<Relationship> getPendingRelationships(@AuthenticationPrincipal AccountabilitySessionUser user) {
        return relationshipRepository.getByUserIdAndStatus(user.getId(), RelationshipStatus.PENDING);
    }

    @PostMapping("/send-request")
    public Relationship addRelationship(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody Long partnerId) {
        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);
        Relationship newRelationshipRequest = new Relationship(thisUser, partner, RelationshipStatus.REQUESTED);
        Relationship newRelationshipPending = new Relationship(partner, thisUser, RelationshipStatus.PENDING);

        relationshipRepository.save(newRelationshipPending);
        return relationshipRepository.save(newRelationshipRequest);
    }

    @PostMapping("/approve-request")
    public Relationship approveRelationship(@AuthenticationPrincipal AccountabilitySessionUser user, @RequestBody Long partnerId) {
        User thisUser = userRepository.findUserById(user.getId());
        User partner = userRepository.findUserById(partnerId);
        Relationship newRelationshipApproved = new Relationship(thisUser, partner, RelationshipStatus.APPROVED);
        return relationshipRepository.save(newRelationshipApproved);
    }

    private UserDto convertUsertoUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;
    }

}
