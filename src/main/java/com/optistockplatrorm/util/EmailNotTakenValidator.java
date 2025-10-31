package com.optistockplatrorm.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.optistockplatrorm.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailNotTakenValidator implements ConstraintValidator<EmailNotTaken, String> {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) return true;
        return !clientRepository.existsByEmail(email);
    }
}