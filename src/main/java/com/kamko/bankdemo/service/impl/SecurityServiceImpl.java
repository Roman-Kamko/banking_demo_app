package com.kamko.bankdemo.service.impl;

import com.kamko.bankdemo.exception.WrongPinException;
import com.kamko.bankdemo.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final PasswordEncoder encoder;

    @Override
    public void verifyPin(String rawPin, String encodedPin, Long accountId) {
        if (!encoder.matches(rawPin, encodedPin)) throw new WrongPinException(accountId);
    }

    @Override
    public String encode(String rawPin) {
        return encoder.encode(rawPin);
    }

}
