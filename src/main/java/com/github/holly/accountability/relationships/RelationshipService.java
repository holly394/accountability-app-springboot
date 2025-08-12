package com.github.holly.accountability.relationships;


import com.github.holly.accountability.user.User;
import com.github.holly.accountability.users.UserDto;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class RelationshipService {
    public List<User> getCleanPartnerList(List<Relationship> relationships, Long userId) {

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

        int indexDelete = 0;
        boolean toDelete = false;

        for(int i=0; i < allPartners.size()-1; i++) {
            Long repeatId = allPartners.get(i).getId();

            for(int j= i+1; j < allPartners.size(); j++) {
                if(repeatId.equals(allPartners.get(j).getId())) {
                    indexDelete = j;
                    toDelete = true;
                }
            }
            if(toDelete) {
                allPartners.remove(indexDelete);
                toDelete = false;
            }
        }

        return allPartners.stream().distinct().toList();
    }

    public UserDto convertUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;
    }

}
