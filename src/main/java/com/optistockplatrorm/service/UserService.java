package com.optistockplatrorm.service;

import com.optistockplatrorm.entity.User;
import com.optistockplatrorm.dto.UserRequestDTO;
import com.optistockplatrorm.dto.UserResponseDTO;
import com.optistockplatrorm.mapper.UserMapper;
import com.optistockplatrorm.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.optistockplatrorm.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDTO findUserByEmailAndByPassword(UserRequestDTO dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé avec l'adresse e-mail fournie."));

        if (!PasswordUtil.verify(dto.password(), user.getPassword())) {
            throw new RuntimeException("Échec de l'authentification : mot de passe invalide.");
        }
        return userMapper.toDto(user);
    }
}