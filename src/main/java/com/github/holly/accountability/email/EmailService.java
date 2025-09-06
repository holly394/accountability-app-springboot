package com.github.holly.accountability.email;

import com.github.holly.accountability.config.GenericResponse;
import com.github.holly.accountability.config.properties.ApplicationProperties;
import com.github.holly.accountability.user.PasswordResetToken;
import com.github.holly.accountability.user.User;
import com.github.holly.accountability.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.UUID;

import static com.github.holly.accountability.email.EmailController.CHANGE_PASSWORD_FROM_TOKEN;

@Component
public class EmailService {

    private final UserService userService;
    private final ApplicationProperties applicationProperties;
    private final JavaMailSender mailSender;

    public EmailService(UserService userService, ApplicationProperties applicationProperties, JavaMailSender mailSender) {

        this.userService = userService;
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
    }

    public SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom("hoyeonyoo@gmail.com");
        return email;
    }

    public SimpleMailMessage constructResetTokenEmail(
            String token, User user) {

        String url = UriComponentsBuilder
                .fromUriString(applicationProperties.getBaseUrl())
                .path(CHANGE_PASSWORD_FROM_TOKEN)
                .queryParam("token", token)
                .build()
                .toUriString();

        String message = """
           Click the link below to reset your password:
           %s""".formatted(url);

        return constructEmail("Reset Password", message, user);
    }

    public String getLinkForPasswordReset(Model model,
                                          String token) {

        String result = userService.validatePasswordResetToken(token);

        if (result != null) {
            return "redirect:/login?message=" + result;
        } else {
            model.addAttribute("token", token);
            return "redirect:/change-password-from-token";
        }
    }

    public GenericResponse sendPasswordEmail(String email)
            throws ResponseStatusException {

        User user = userService.findUserByEmail(email)
                .orElse(null);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that email not found");
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        mailSender.send(constructResetTokenEmail(token, user));

        return new GenericResponse("Email sent!", "no");
    }

}
