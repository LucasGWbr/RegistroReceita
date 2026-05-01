package br.univates.service;


import br.univates.dtos.UserDto;
import br.univates.model.Users;
import br.univates.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public Users createUser(UserDto userDTO) {
        String password = encoder.encode(userDTO.password());
        try {
            Users users = new Users();
            BeanUtils.copyProperties(userDTO, users);
            users.setPassword(password);
            users.setEmail(userDTO.login());
            return userRepository.save(users);
        } catch (Exception e) {
            return null;
        }
    }
}
