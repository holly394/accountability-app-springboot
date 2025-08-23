//WHY: to convert any user (partner lists, etc.) to UserDto whenever needed

package com.github.holly.accountability.user;

import org.springframework.stereotype.Component;

@Component
public class UserService {
    public UserDto convertUserToDto(User user){

        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());

        return userDto;
    }
}
