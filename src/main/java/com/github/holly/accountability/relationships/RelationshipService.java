package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.User;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class RelationshipService {

    public List<User> deduplicateRelationshipsForUser(List<Relationship> relationships, Long userId) {

        List<User> allPartners = new ArrayList<>();
        List<User> usersNotCurrent = relationships.stream()
                .map(Relationship::getUser)
                .filter(responseUser -> !Objects.equals(responseUser.getId(), userId))
                .distinct()
                .toList();

        List<User> partnersNotCurrent = relationships.stream()
                .map(Relationship::getPartner)
                .filter(responsePartner -> !Objects.equals(responsePartner.getId(), userId))
                .distinct()
                .toList();

        allPartners.addAll(usersNotCurrent);
        allPartners.addAll(partnersNotCurrent);

        return allPartners.stream().distinct().toList();
    }

}
