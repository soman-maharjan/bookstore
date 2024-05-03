package com.example.bookstore.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.jsonwebtoken.Claims;
import lombok.Getter;

public final class JwtAccessToken implements JwtToken {
    private final String rawToken;
    @Getter
    @JsonIgnore private Claims claims;

    protected JwtAccessToken(final String token, Claims claims) {
        this.rawToken = token;
        this.claims = claims;
    }

    public String getToken() {
        return this.rawToken;
    }

}
