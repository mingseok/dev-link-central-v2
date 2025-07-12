package dev.devlink.member.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpCommand {

    private final String name;
    private final String email;
    private final String nickname;
    private final String password;

    @Builder
    public SignUpCommand(
            String name,
            String email,
            String nickname,
            String password
    ) {
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }
}
