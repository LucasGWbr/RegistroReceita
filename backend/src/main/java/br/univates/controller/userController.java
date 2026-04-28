package br.univates.controller;

import br.univates.dtos.loginDTO;
import br.univates.dtos.userDTO;
import br.univates.model.users;
import br.univates.service.EmailService;
import br.univates.service.loginService;
import br.univates.service.userService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class userController {

    private final loginService loginService;
    private final userService userService;
    private final EmailService emailService;
    public userController(loginService loginService, userService userService, EmailService emailService) {
        this.loginService = loginService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginDTO loginDTO) {
        users user = loginService.login(loginDTO);
        if(user != null){
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody userDTO userDTO) {
        users user = userService.createUser(userDTO);
        if(user != null){
            emailService.sentMail(user.getLogin(),"Bem vindo a plataforma!","Olá, "+user.getName()+ " seja bem vindo a plataforma");
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }


}
