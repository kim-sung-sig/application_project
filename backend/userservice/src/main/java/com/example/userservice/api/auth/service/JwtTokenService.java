package com.example.userservice.api.auth.service;

import org.springframework.stereotype.Service;

import com.example.userservice.api.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    
}
