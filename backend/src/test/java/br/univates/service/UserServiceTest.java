package br.univates.service;

import br.univates.dtos.UserDto;
import br.univates.model.Users;
import br.univates.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private Users sampleUser;
    private UserDto sampleDtoWorking;


    @BeforeEach
    void setUp() {
        sampleDtoWorking = new UserDto("Lucas","lucas","lucas@gmail.com");



        sampleUser = new Users();
        sampleUser.setId(1L);
        sampleUser.setName("Lucas");
        sampleUser.setEmail("lucas@gmail.com");
        sampleUser.setPassword(encoder.encode("Lucas"));
        sampleUser.setCreatedAt(LocalDateTime.now());
    }

    // 23 Tenta criar usuario com sucesso
    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.save(any(Users.class))).thenReturn(sampleUser);

        Users user = userService.createUser(sampleDtoWorking);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("lucas@gmail.com");
        verify(userRepository, times(1)).save(any(Users.class));
    }
}
