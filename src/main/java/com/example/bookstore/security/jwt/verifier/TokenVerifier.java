package com.example.bookstore.security.jwt.verifier;

public interface TokenVerifier {
    public boolean verify(String jti);
}
