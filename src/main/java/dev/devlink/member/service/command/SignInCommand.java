package dev.devlink.member.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInCommand {

    private final String email;
    private final String password;

    @Builder
    public SignInCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }
}