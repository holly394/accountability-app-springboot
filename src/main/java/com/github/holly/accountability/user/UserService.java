
package com.github.holly.accountability.user;

import com.github.holly.accountability.config.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class UserService {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public UserDto convertUserToDto(User user){

        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getUsername());

        return userDto;
    }

    public User findUserById(Long userId){
        return userRepository.findUserById(userId);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public List<User> findUsersByNameExceptOne(String name, Long currentUserId){
        List<User> searchList = userRepository.findUsersByUsernameContainsIgnoreCase(name);
        String excludedName = findUserById(currentUserId).getUsername();

        return searchList.stream()
                .filter(thisPartner ->
                        !thisPartner.getUsername().equals(excludedName))
                .toList();
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    public GenericResponse setNewPassword(String token,
                                         String newPassword,
                                         String newPasswordRepeated)
            throws ResponseStatusException {

        PasswordResetToken thisToken = passwordResetTokenRepository.findByToken(token);

        // Find the user by username or throw an exception if not found
        User user = thisToken.getUser();

        if(user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        // Check if the current password matches
        if (!Objects.equals(newPassword, newPasswordRepeated)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        if (!patternMatches(newPassword, "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,20}$")) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must have at least one small letter,\n" +
                                                                        "at least one capital letter,\n" +
                                                                        "at least one digit,\n" +
                                                                        "at least one special symbol,\n" +
                                                                        "and between 8 to 20 characters.");
        }

        // Encode and set the new password, then save the user
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new GenericResponse("Password updated successfully.", "no");
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    private static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

}
