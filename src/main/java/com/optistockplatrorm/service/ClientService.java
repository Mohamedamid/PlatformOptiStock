package com.optistockplatrorm.service;

import com.optistockplatrorm.entity.Client;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.dto.ClientRequestDTO;
import com.optistockplatrorm.dto.ClientResponseDTO;
import com.optistockplatrorm.mapper.ClientMapper;
import com.optistockplatrorm.util.PasswordUtil;
import org.springframework.stereotype.Service;
import com.optistockplatrorm.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private  ClientMapper clientMapper;

    public ClientResponseDTO createClient(ClientRequestDTO dto) {
        Client client = Client.builder()
                .firstName(dto.firstName()).lastName(dto.lastName())
                .email(dto.email()).password(PasswordUtil.hash(dto.password()))
                .phoneNumber(dto.phone()).role(Role.CLIENT).active(true)
                .createdAt(LocalDateTime.now()).build();

        return clientMapper.toDto(clientRepository.save(client));
    }
}
