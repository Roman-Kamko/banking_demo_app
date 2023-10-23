package com.kamko.bankdemo.service;

public interface SecurityService {

    void verifyPin(String rawPin, String encodedPin, Long accountId);

    String encode(String rawPin);

}
