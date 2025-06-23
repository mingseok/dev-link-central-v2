package dev.devlink.common.jwt;

public interface TokenProvider {

    JwtToken generateToken(Long memberId);

    boolean validateToken(String token);

    Long extractMemberId(String token);
}
