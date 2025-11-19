package com.amf.promotora.service;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Client;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.util.CpfUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setup() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Test
    void testCreateSuccess() {
        ClientDTO dto = new ClientDTO();
        dto.setFullName("Alexandre");
        dto.setCpf("12345678909");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));

        Client saved = new Client();
        saved.setId("1");
        saved.setFullName(dto.getFullName());
        saved.setCpf(dto.getCpf());
        saved.setBirthDate(dto.getBirthDate());

        // Mock static CpfUtils.isValid()
        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("12345678909")).thenReturn(true);
            when(clientRepository.existsByCpf("12345678909")).thenReturn(false);
            when(clientRepository.save(any(Client.class))).thenReturn(saved);

            Client result = clientService.create(dto);

            assertEquals("1", result.getId());
            assertEquals("Alexandre", result.getFullName());
            mockedCpf.verify(() -> CpfUtils.isValid("12345678909"), times(1));
        }
    }

    @Test
    void testCreateThrowsInvalidCpf() {
        ClientDTO dto = new ClientDTO();
        dto.setCpf("111");

        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("111")).thenReturn(false);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> clientService.create(dto));

            assertEquals("CPF inválido", ex.getMessage());
        }
    }

    @Test
    void testCreateThrowsCpfAlreadyExists() {
        ClientDTO dto = new ClientDTO();
        dto.setCpf("12345678909");

        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("12345678909")).thenReturn(true);
            when(clientRepository.existsByCpf("12345678909")).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> clientService.create(dto));

            assertEquals("CPF já cadastrado", ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // FIND ALL
    // ---------------------------------------------------------
    @Test
    void testFindAll() {
        List<Client> list = List.of(new Client(), new Client());
        when(clientRepository.findAll()).thenReturn(list);

        List<Client> result = clientService.findAll();

        assertEquals(2, result.size());
    }

    // ---------------------------------------------------------
    // SAVE
    // ---------------------------------------------------------
    @Test
    void testSaveSuccess() {
        Client client = new Client();
        client.setId("1");
        client.setCpf("12345678909");

        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("12345678909")).thenReturn(true);
            when(clientRepository.findByCpf("12345678909")).thenReturn(Optional.of(client));
            when(clientRepository.save(client)).thenReturn(client);

            Client result = clientService.save(client);

            assertEquals(client, result);
        }
    }

    @Test
    void testSaveThrowsInvalidCpf() {
        Client client = new Client();
        client.setCpf("000");

        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("000")).thenReturn(false);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> clientService.save(client));

            assertEquals("CPF inválido", ex.getMessage());
        }
    }

    @Test
    void testSaveThrowsCpfAlreadyExistsWithDifferentId() {
        Client client = new Client();
        client.setId("1");
        client.setCpf("12345678909");

        Client other = new Client();
        other.setId("2");
        other.setCpf("12345678909");

        try (MockedStatic<CpfUtils> mockedCpf = mockStatic(CpfUtils.class)) {

            mockedCpf.when(() -> CpfUtils.isValid("12345678909")).thenReturn(true);
            when(clientRepository.findByCpf("12345678909")).thenReturn(Optional.of(other));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> clientService.save(client));

            assertEquals("CPF já cadastrado", ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // FIND BY ID
    // ---------------------------------------------------------
    @Test
    void testFindByIdOptional() {
        Client c = new Client();
        c.setId("1");

        when(clientRepository.findById("1")).thenReturn(Optional.of(c));

        Optional<Client> result = clientService.findByIdOptional("1");

        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Test
    void testDelete() {
        doNothing().when(clientRepository).deleteById("1");

        clientService.delete("1");

        verify(clientRepository, times(1)).deleteById("1");
    }
}
