package dev.devlink.member.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInServiceDto {

    private final String email;
    private final String password;

    @Builder
    public SignInServiceDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}