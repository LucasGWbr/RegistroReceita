package br.univates.service;

import br.univates.dtos.loginDTO;
import br.univates.model.users;
import br.univates.repository.userRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class loginService {
    private final BCryptPasswordEncoder encoder;
    private final userRepository userRepository;

    public loginService(BCryptPasswordEncoder encoder, userRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }
    public users login(loginDTO loginDTO) {
        users user = userRepository.findByLogin(loginDTO.login());
        if (user != null && encoder.matches(loginDTO.password(), user.getPassword())) {
            return user;
        }
        return null;
    }
}
