package com.Fitness.userService.controller;

import com.Fitness.userService.dto.RegisterRequest;
import com.Fitness.userService.dto.UserResponse;
import com.Fitness.userService.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
 public class UserController {

    private UserService userservice;



    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userservice.getUserRepo(userId));
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userservice.register(request));
    }


    // InterService Comm
    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUserId(@PathVariable String userId){
        return ResponseEntity.ok(userservice.validateUserById(userId));
    }
}
