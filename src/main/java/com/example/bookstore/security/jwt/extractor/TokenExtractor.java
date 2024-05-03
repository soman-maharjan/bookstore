package com.example.bookstore.security.jwt.extractor;

public interface TokenExtractor {
    public String extract(String payload);
}