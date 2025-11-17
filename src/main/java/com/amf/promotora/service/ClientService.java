package com.amf.promotora.service;

import com.amf.promotora.dto.ClientDTO;
import com.amf.promotora.model.Client;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.util.CpfUtils;
import com.amf.promotora.exception.BusinessException;
import org.springframework.stereotype.Service;

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
}
