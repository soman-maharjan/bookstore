package com.example.bookstore.user;

import java.util.List;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class UserContext {
    private final String username;
    private final List<GrantedAuthority> authorities;

    private UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserContext create(String username, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank: " + username);
        return new UserContext(username, authorities);
    }

}