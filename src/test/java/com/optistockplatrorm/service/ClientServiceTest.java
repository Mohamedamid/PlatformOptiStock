package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.ClientRequestDTO;
import com.optistockplatrorm.dto.ClientResponseDTO;
import com.optistockplatrorm.entity.Client;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.mapper.ClientMapper;
import com.optistockplatrorm.repository.ClientRepository;
import com.optistockplatrorm.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Add this annotation
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordUtil passwordUtil; // Mock the utility class

    @InjectMocks
    private ClientService clientService;

    private ClientRequestDTO clientRequestDTO;
    private Client mockClientEntity;
    private ClientResponseDTO clientResponseDTO;
    private final String HASHED_PASSWORD = "hashed_password";

    @BeforeEach
    void setUp() {
        clientRequestDTO = ClientRequestDTO.builder()
                .firstName("Karim").lastName("Dafali").email("karim@example.com")
                .password("secure123").phone("+212600000000").build();

        mockClientEntity = Client.builder()
                .id(1L).firstName("Karim").lastName("Dafali").email("karim@example.com")
                .password(HASHED_PASSWORD).phoneNumber("+212600000000").role(Role.CLIENT)
                .active(true).createdAt(LocalDateTime.now()).build();

        clientResponseDTO = new ClientResponseDTO(1L, "Karim", "Dafali", "karim@example.com",
                LocalDateTime.now(), "+212600000000", Role.CLIENT.name(), true);

        // Mock static method call on PasswordUtil
        mockStatic(PasswordUtil.class);
    }

    @Test
    void testCreateClientSuccess() {
        // Mock Hashing
        when(PasswordUtil.hash(clientRequestDTO.password())).thenReturn(HASHED_PASSWORD);

        // Mock Save/Mapping
        when(clientRepository.save(any(Client.class))).thenReturn(mockClientEntity);
        when(clientMapper.toDto(mockClientEntity)).thenReturn(clientResponseDTO);

        ClientResponseDTO result = clientService.createClient(clientRequestDTO);

        assertNotNull(result);
        assertEquals("Karim", result.firstName());
        assertEquals(Role.CLIENT.name(), result.role());

        // Capture the entity to verify fields before saving
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());

        Client capturedClient = clientCaptor.getValue();
        assertEquals(HASHED_PASSWORD, capturedClient.getPassword());
        assertEquals(Role.CLIENT, capturedClient.getRole());
        assertTrue(capturedClient.isActive());
        assertNotNull(capturedClient.getCreatedAt());

        // Verify hash was called once
        verify(PasswordUtil.class, times(1));
        PasswordUtil.hash("secure123");
    }
}