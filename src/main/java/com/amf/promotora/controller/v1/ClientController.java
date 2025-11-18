package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.model.Client;
import com.amf.promotora.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/clients")
@Validated
@Tag(name = "Clients", description = "API para gerenciamento de clientes")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Cadastrar novo cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já cadastrado")
    })
    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody ClientDTO dto) {
        Client c = clientService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/clients/" + c.getId())).body(c);
    }
}
