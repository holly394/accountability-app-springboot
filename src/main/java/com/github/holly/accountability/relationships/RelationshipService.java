package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.UserDto;
import com.github.holly.accountability.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class RelationshipService {

    @Autowired
    private UserService userService;

    public RelationshipDto convertRelationshipToRelationshipData(Relationship relationship) {

        return new RelationshipDto(
                relationship.getId(),
                relationship.getStatus(),
                userService.convertUserToDto(relationship.getUser()),
                userService.convertUserToDto(relationship.getPartner()));
    }

    /**
     * Since this data is used to display to the current user,
     * the relationship gets flipped to where each relationship is seen
     * from the perspective of the logged-in user.
     */
    public RelationshipDto convertToRelationshipDataWhereGivenUserIsNeverPartner(Long currentUserId,
                                                                                 RelationshipDto relationshipDto){

        if (Objects.equals(relationshipDto.getPartner().getId(), currentUserId)) {
            return relationshipDto.flipped();
        }

        return relationshipDto;
    }

    public RelationshipDto convertToRelationshipDataWhereGivenUserIsNeverPartner(Long currentUserId,
                                                                                 Relationship relationship){

        RelationshipDto relationshipDto = convertRelationshipToRelationshipData(relationship);

        if (Objects.equals(relationshipDto.getPartner().getId(), currentUserId)) {
            return relationshipDto.flipped();
        }

        return relationshipDto;
    }

    public List<UserDto> getPartnersFromGivenListForGivenUser(Long userId,
                                                              List<Relationship> relationships) {

        List<RelationshipDto> partnersSameDirection = relationships.stream()
                .map(res ->
                        convertToRelationshipDataWhereGivenUserIsNeverPartner(userId, res))
                .toList();

        return partnersSameDirection.stream()
                .map(RelationshipDto::getPartner)
                .toList();
    }
}
