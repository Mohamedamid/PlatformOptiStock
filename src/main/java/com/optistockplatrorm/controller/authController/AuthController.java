package com.optistockplatrorm.controller.authController;

import com.optistockplatrorm.dto.*;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.service.ClientService;
import com.optistockplatrorm.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String bienvenue() {
        return "Bienvenue sur OptiStock! Utilisez /api/login pour vous connecter ou /api/register pour créer un compte";
    }

    @PostMapping("/register")
    public ResponseEntity<ClientResponseDTO> register(@Valid @RequestBody ClientRequestDTO dto) {
        ClientResponseDTO createdClient = clientService.createClient(dto);
        return ResponseEntity.ok(createdClient);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestDTO dto, HttpSession session) {
        UserResponseDTO userLogged = userService.findUserByEmailAndByPassword(dto);

        session.setAttribute("user", userLogged);
        session.setAttribute("role", userLogged.role());

        String redirectUrl = userLogged.role() == Role.CLIENT
                ? "/client/home"
                : "/admin/dashboard";

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("role", userLogged.role());
        response.put("redirect", redirectUrl);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Object user = session.getAttribute("user");
        Object role = session.getAttribute("role");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Aucun utilisateur connecté"));
        }

        return ResponseEntity.ok(Map.of(
                "user", user,
                "role", role
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }
}
