package com.github.holly.accountability.email;

public class ResetPasswordDto {
    private String password;
    private String passwordRepeated;

    public ResetPasswordDto() {

    }

    public ResetPasswordDto(String password, String passwordRepeated) {
        this.password = password;
        this.passwordRepeated = passwordRepeated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeated() {
        return passwordRepeated;
    }

    public void setPasswordRepeated(String passwordRepeated) {
        this.passwordRepeated = passwordRepeated;
    }
}
