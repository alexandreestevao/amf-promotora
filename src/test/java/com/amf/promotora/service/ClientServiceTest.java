package com.amf.promotora.service;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Client;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.util.CpfUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    @Test
    void create_ShouldSaveClient_WhenCpfValidAndNotExists() {
        ClientDTO dto = new ClientDTO();
        dto.setFullName("Alexandre Estevão");
        dto.setCpf("12345678901");
        dto.setBirthDate(LocalDate.parse("1980-01-01"));

        // Mock do CPF válido
        mockStaticCpfUtils(true);

        // Mock para CPF ainda não cadastrado
        when(clientRepository.existsByCpf(dto.getCpf())).thenReturn(false);

        Client savedClient = new Client();
        savedClient.setFullName(dto.getFullName());
        savedClient.setCpf(dto.getCpf());
        savedClient.setBirthDate(dto.getBirthDate());
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        Client result = clientService.create(dto);

        assertNotNull(result);
        assertEquals("Alexandre Estevão", result.getFullName());
        assertEquals("12345678901", result.getCpf());
        assertEquals(LocalDate.parse("1980-01-01"), result.getBirthDate());

        // Verifica que o repository.save foi chamado com o Client correto
        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertEquals("Alexandre Estevão", captor.getValue().getFullName());
    }

    @Test
    void create_ShouldThrowBusinessException_WhenCpfInvalid() {
        ClientDTO dto = new ClientDTO();
        dto.setFullName("Alexandre Estevão");
        dto.setCpf("123"); // CPF inválido
        dto.setBirthDate(LocalDate.parse("1980-01-01"));

        mockStaticCpfUtils(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> clientService.create(dto));
        assertEquals("CPF inválido", ex.getMessage());

        verify(clientRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowBusinessException_WhenCpfAlreadyExists() {
        ClientDTO dto = new ClientDTO();
        dto.setFullName("Alexandre Estevão");
        dto.setCpf("12345678901");
        dto.setBirthDate(LocalDate.parse("1980-01-01"));

        mockStaticCpfUtils(true);

        when(clientRepository.existsByCpf(dto.getCpf())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> clientService.create(dto));
        assertEquals("CPF já cadastrado", ex.getMessage());

        verify(clientRepository, never()).save(any());
    }

    // Método auxiliar para mockar CpfUtils.isValid
    private void mockStaticCpfUtils(boolean returnValue) {
        try (var mocked = Mockito.mockStatic(CpfUtils.class)) {
            mocked.when(() -> CpfUtils.isValid(anyString())).thenReturn(returnValue);
        }
    }
}
