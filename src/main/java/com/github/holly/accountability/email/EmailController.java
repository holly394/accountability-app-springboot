package com.github.holly.accountability.email;

import com.github.holly.accountability.config.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/email")
public class EmailController {

    public static final String CHANGE_PASSWORD_FROM_TOKEN = "/change-password-from-token";

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @ResponseBody
    @GetMapping("/send-token")
    public GenericResponse sendPasswordEmail(@RequestParam String email) {
        try {
            return emailService.sendPasswordEmail(email);
        } catch (Exception e) {
            return new GenericResponse(e.getMessage());
        }
    }

    @GetMapping(CHANGE_PASSWORD_FROM_TOKEN + "/{token}")
    public String showChangePasswordFromTokenPage(@PathVariable("token") String token) {
        boolean isValid = emailService.validatePasswordResetToken(token);
        if (!isValid) {
            return "redirect:/login";
        }
        return "redirect:%s?token=%s".formatted(CHANGE_PASSWORD_FROM_TOKEN, token);
    }

    //change this so it's a JSON object being given with a PostMapping
    @ResponseBody
    @PostMapping("/set-new-password/{token}")
    public GenericResponse changePasswordFromToken(@PathVariable("token") String token,
                                                   @RequestBody ResetPasswordDto passwordDto) {
        try {
            return emailService.setNewPassword(token, passwordDto);
        } catch (Exception e) {
            return new GenericResponse(e.getMessage(), "yes");
        }
    }
    //NewTest@1

}
