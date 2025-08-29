//WHY: to convert any user (partner lists, etc.) to UserDto whenever needed

package com.github.holly.accountability.user;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto convertUserToDto(User user){

        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());

        return userDto;
    }

    public User findUserById(Long userId){
        return userRepository.findUserById(userId);
    }

    public List<User> findUsersByName(String name){
        return userRepository.findUsersByUsernameContainsIgnoreCase(name);
    }

    public List<User> findUsersByNameExceptOne(String name, Long currentUserId){
        List<User> searchList = userRepository.findUsersByUsernameContainsIgnoreCase(name);
        String excludedName = findUserById(currentUserId).getUsername();

        return searchList.stream()
                .filter(thisPartner ->
                        !thisPartner.getUsername().equals(excludedName))
                .toList();
    }


}
