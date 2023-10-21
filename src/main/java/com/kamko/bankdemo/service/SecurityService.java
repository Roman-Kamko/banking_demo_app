package com.kamko.bankdemo.service;

import com.kamko.bankdemo.exception.WrongPinException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder encoder;

    public void verifyPin(String rawPin, String encodedPin, Long accountId) {
        if (!encoder.matches(rawPin, encodedPin)) throw new WrongPinException(accountId);
    }

    public String encode(String rawPin) {
        return encoder.encode(rawPin);
    }

}
