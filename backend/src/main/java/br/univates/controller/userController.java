package br.univates.controller;

import br.univates.dtos.loginDTO;
import br.univates.dtos.userDTO;
import br.univates.model.users;
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
    public userController(loginService loginService, userService userService) {
        this.loginService = loginService;
        this.userService = userService;
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
            return ResponseEntity.ok(user);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }


}
