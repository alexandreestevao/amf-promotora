package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.model.Client;
import com.amf.promotora.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void testCreateClient() throws Exception {
        Client client = new Client();
        client.setId("client1");
        client.setFullName("Alexandre Estevão");
        client.setCpf("12345678901");
        client.setBirthDate(LocalDate.parse("1980-01-01"));

        when(clientService.create(any(ClientDTO.class))).thenReturn(client);

        ClientDTO dto = new ClientDTO();
        dto.setFullName("Alexandre Estevão");
        dto.setCpf("12345678901");
        dto.setBirthDate(LocalDate.parse("1980-01-01"));

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("client1"))
                .andExpect(jsonPath("$.fullName").value("Alexandre Estevão"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.birthDate").value("1980-01-01"));
    }
}
