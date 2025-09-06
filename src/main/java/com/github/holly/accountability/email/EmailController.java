package com.github.holly.accountability.email;

import com.github.holly.accountability.config.GenericResponse;
import com.github.holly.accountability.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/email")
public class EmailController {

    public static final String CHANGE_PASSWORD_FROM_TOKEN = "/change-password-from-token";

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public EmailController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/send-token")
    public GenericResponse sendPasswordEmail(@RequestParam String email) {
        try {
            return emailService.sendPasswordEmail(email);
        } catch (Exception e) {
            return new GenericResponse(e.getMessage());
        }
    }

    @GetMapping(CHANGE_PASSWORD_FROM_TOKEN)
    public String showChangePasswordFromTokenPage(Model model,
                                                  @RequestParam("token") String token) {

        return emailService.getLinkForPasswordReset(model, token);
    }

    @GetMapping("/set-new-password")
    public GenericResponse changePasswordFromToken(@RequestParam("token") String token,
                                                   @RequestParam("password") String newPassword,
                                                   @RequestParam("passwordRepeated") String newPasswordRepeated) {
        try {
            return userService.setNewPassword(token, newPassword, newPasswordRepeated);
        } catch (Exception e) {
            return new GenericResponse(e.getMessage(), "yes");
        }
    }

}
