package com.optistockplatrorm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String bienvenue () {
        return "Bienvenue sur OptiStock! Utilisez /login pour vous connecter ou /register pour créer un compte";
    }
}
