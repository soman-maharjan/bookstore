package com.example.bookstore.security.config;

import java.util.Arrays;
import java.util.List;

import com.example.bookstore.security.CustomUsernamePasswordAuthenticationFilter;
import com.example.bookstore.security.auth.*;
import com.example.bookstore.security.jwt.JwtFilter;
import com.example.bookstore.security.jwt.JwtProvider;
import com.example.bookstore.security.jwt.SkipPathRequestMatcher;
import com.example.bookstore.security.jwt.extractor.TokenExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String REFRESH_TOKEN_URL = "/api/auth/token";
    public static final String API_ROOT_URL = "/api/**";

    @Autowired
    private AuthEntryPoint authenticationEntryPoint;

    @Autowired private AuthSuccessHandler successHandler;
    @Autowired private AuthFailureHandler failureHandler;
//    @Autowired private AuthProvider ajaxAuthenticationProvider;
//    @Autowired private JwtProvider jwtAuthenticationProvider;

    @Autowired private TokenExtractor tokenExtractor;

//    @Autowired private AuthenticationManager authenticationManager;

    @Autowired private ObjectMapper objectMapper;

    protected AuthFilter buildAuthFilter() throws Exception {
        AuthFilter filter = new AuthFilter(WebSecurityConfig.AUTHENTICATION_URL, successHandler, failureHandler, objectMapper);
//        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    protected JwtFilter buildJwtTokenAuthFilter(List<String> pathsToSkip, String pattern) throws Exception {
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        JwtFilter filter = new JwtFilter(failureHandler, tokenExtractor, matcher);
//        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

//    public AuthenticationManagerBuilder configure(AuthenticationManagerBuilder auth) {
//        auth.authenticationProvider(ajaxAuthenticationProvider);
//        auth.authenticationProvider(jwtAuthenticationProvider);
//        return auth;
//    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter authenticationFilter(
            HttpSecurity http,
            AuthSuccessHandler customAuthenticationSuccessHandler,
            AuthFailureHandler customAuthenticationFailureHandler) throws Exception {
        CustomUsernamePasswordAuthenticationFilter authenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
//        AuthenticationManager authenticationManager = authenticationManager(
//                http.getSharedObject(AuthenticationConfiguration.class));

        AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/v1/auth/sign-in", "POST");

//        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setRequiresAuthenticationRequestMatcher(antPathRequestMatcher);
        authenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        authenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);

        return authenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthEntryPoint restAuthenticationEntryPoint) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(
                AUTHENTICATION_URL,
                REFRESH_TOKEN_URL,
                "/console"
        );

         http
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling((exception) -> exception
                                .authenticationEntryPoint(restAuthenticationEntryPoint)
//                                .authenticationEntryPoint(this.authenticationEntryPoint)
                )

                 .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers((permitAllEndpointList.toArray(new String[0])))
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(API_ROOT_URL).authenticated()
                )
                .addFilterBefore(buildAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(buildJwtTokenAuthFilter(permitAllEndpointList,
                        API_ROOT_URL), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}