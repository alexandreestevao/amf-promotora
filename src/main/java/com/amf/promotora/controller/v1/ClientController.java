package com.amf.promotora.controller.v1;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.model.Client;
import com.amf.promotora.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/clients")
@Validated
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody ClientDTO dto) {
        Client c = clientService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/clients/" + c.getId())).body(c);
    }
}
