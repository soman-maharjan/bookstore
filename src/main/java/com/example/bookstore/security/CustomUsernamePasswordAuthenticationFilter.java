package com.example.bookstore.security;

import com.example.bookstore.miscellaneous.util.HttpServletRequestUtils;
import com.example.bookstore.security.auth.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        byte[] bytes = HttpServletRequestUtils.getRequestReaderByte(request);
        Login authRequest = HttpServletRequestUtils.getAuthRequest(bytes);
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        setDetails(request, token);

        return this.getAuthenticationManager().authenticate(token);
    }
}
