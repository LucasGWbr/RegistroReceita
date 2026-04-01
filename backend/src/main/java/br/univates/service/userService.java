package br.univates.service;

import br.univates.dtos.userDTO;
import br.univates.model.users;
import br.univates.repository.userRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class userService {
    private final BCryptPasswordEncoder encoder;
    private final userRepository userRepository;

    public userService(BCryptPasswordEncoder encoder, userRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public users createUser(userDTO userDTO) {
        String password = encoder.encode(userDTO.password());
        try{
            users users = new users();
            BeanUtils.copyProperties(userDTO, users);
            users.setPassword(password);
            return userRepository.save(users);
        }catch (Exception e){
            return null;
        }
    }
}
