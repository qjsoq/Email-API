package com.example.emailapp.controler;

import com.example.emailapp.domain.HttpResponse;
import com.example.emailapp.domain.User;
import com.example.emailapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody User user){
        User newUser = userService.saveUser(user);
        System.out.println(newUser);
        return ResponseEntity.created(URI.create("")).body(
                HttpResponse.builder()
                        .code(201)
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", newUser))
                        .message("User created ")
                        .httpStatus(HttpStatus.CREATED)
                        .build());
    }
}
