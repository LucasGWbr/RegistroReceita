package br.univates.service;

import br.univates.config.JwtService;
import br.univates.dtos.AuthenticationResponseDTO;
import br.univates.dtos.LoginDto;
import br.univates.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    public AuthenticationResponseDTO login(LoginDto loginDTO) {
        // 1. AUTENTICAR
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.login(), // ou getUsername()
                        loginDTO.password()
                )
        );

        // 2. GERAR O TOKEN
        var user = userRepository.findByEmail(loginDTO.login())
                .orElseThrow();
        // Gera o token JWT
        String jwtToken = jwtService.generateToken(user); // 'user' deve implementar UserDetails

        // 3. RETORNAR O TOKEN
        return new AuthenticationResponseDTO(jwtToken,user.getName(),user.getEmail(),user.getId());
    }
}
