package br.univates.service;

import br.univates.config.JwtService;
import br.univates.dtos.AuthenticationResponseDTO;
import br.univates.dtos.LoginDto;
import br.univates.model.Users;
import br.univates.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Users sampleUser;
    private LoginDto sampleDtoWorking;
    private LoginDto sampleDtoNotWorking;


    @BeforeEach
    void setUp() {
        sampleDtoWorking = new LoginDto("lucas@gmail.com","Lucas");
        sampleDtoNotWorking = new LoginDto("lucas@gmail.com","errado");



        sampleUser = new Users();
        sampleUser.setId(1L);
        sampleUser.setName("Lucas");
        sampleUser.setEmail("lucas@gmail.com");
        sampleUser.setPassword(encoder.encode("Lucas"));
        sampleUser.setCreatedAt(LocalDateTime.now());
    }

    // ─── LOGIN ──────────────────────────────────────────────────────────────

    // 21 Tentar logins com sucesso
    @Test
    void shouldLoginSuccessfully() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(sampleUser, null));

        when(userRepository.findByEmail("lucas@gmail.com")).thenReturn(Optional.of(sampleUser));

        when(jwtService.generateToken(any())).thenReturn("token-fake-123");

        AuthenticationResponseDTO result = loginService.login(sampleDtoWorking);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("lucas@gmail.com");
        assertThat(result.token()).isNotNull();
    }

    // 22 Falhar ao tentar logar com senha errada
    @Test
    void shouldFailToLoginWithWrongPassword() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Senha incorreta"));

        assertThatThrownBy(() -> {
            loginService.login(sampleDtoNotWorking);
        }).isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
