package br.univates.controller;

import br.univates.dtos.AuthenticationResponseDTO;
import br.univates.dtos.LoginDto;
import br.univates.dtos.UserDto;
import br.univates.model.Users;
import br.univates.service.EmailService;
import br.univates.service.LoginService;
import br.univates.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final LoginService loginService;
    private final UserService userService;
    private final EmailService emailService;
    public UserController(LoginService loginService, UserService userService, EmailService emailService) {
        this.loginService = loginService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDTO) {
        AuthenticationResponseDTO user = loginService.login(loginDTO);
        if(user != null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDTO) {
        Users user = userService.createUser(userDTO);
        if(user != null){
            emailService.sentMail(user.getEmail(),"Bem vindo a plataforma!","Olá, "+user.getName()+ " seja bem vindo a plataforma");
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }


}
