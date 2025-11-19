package com.amf.promotora.service;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.exception.BusinessException;
import com.amf.promotora.model.Client;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.util.CpfUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client create(ClientDTO dto) {
        if (!CpfUtils.isValid(dto.getCpf())) {
            throw new BusinessException("CPF inválido");
        }
        if (clientRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }

        Client c = new Client();
        c.setFullName(dto.getFullName());
        c.setCpf(dto.getCpf());
        c.setBirthDate(dto.getBirthDate());

        return clientRepository.save(c);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client save(Client client) {
        if (!CpfUtils.isValid(client.getCpf())) {
            throw new BusinessException("CPF inválido");
        }

        clientRepository.findByCpf(client.getCpf())
                .filter(found -> !found.getId().equals(client.getId()))
                .ifPresent(found -> {
                    throw new BusinessException("CPF já cadastrado");
                });

        return clientRepository.save(client);
    }

    public Optional<Client> findByIdOptional(String id) {
        return clientRepository.findById(id);
    }

    public void delete(String id) {
        clientRepository.deleteById(id);
    }
}

