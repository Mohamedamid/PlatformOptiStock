package com.optistockplatrorm.controller.authController;

import com.optistockplatrorm.dto.*;
import com.optistockplatrorm.service.ClientService;
import com.optistockplatrorm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;

    @PostMapping("/api/register")
    public ResponseEntity<ClientResponseDTO> register(@Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO createdClient = clientService.createClient(dto);
        return ResponseEntity.ok(createdClient);
    }




    @PostMapping("/api/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO userLoged = userService.findUserByEmailAndByPassword(dto);
        return ResponseEntity.ok(userLoged);
    }

}