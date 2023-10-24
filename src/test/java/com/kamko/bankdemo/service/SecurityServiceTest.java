package com.kamko.bankdemo.service;

import com.kamko.bankdemo.exception.WrongPinException;
import com.kamko.bankdemo.service.impl.SecurityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private SecurityServiceImpl securityService;

    private static final String CORRECT_PIN = "1111";
    private static final String WRONG_PIN = "1112";

    @Test
    void verifyPin_wrongPinException() {
        doReturn(false).when(encoder).matches(anyString(), anyString());
        assertAll(
                () -> assertThatExceptionOfType(WrongPinException.class)
                        .isThrownBy(() -> securityService.verifyPin(WRONG_PIN, CORRECT_PIN, 1L)),
                () -> verify(encoder, only()).matches(anyString(), anyString())
        );
    }

    @Test
    void encode_success() {
        String result = "1111";
        doReturn(result).when(encoder).encode(anyString());
        assertAll(
                () -> assertThat(securityService.encode(CORRECT_PIN)).isEqualTo(result),
                () -> verify(encoder, only()).encode(anyString())
        );
    }

}