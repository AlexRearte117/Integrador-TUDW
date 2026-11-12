package com.taller.adapter.data.repository;

import com.taller.adapter.data.entity.DTO_Cliente;
import com.taller.adapter.data.jpa.JPA_Cliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class Repository_Cliente {

    private final JPA_Cliente jpaCliente;

    public Repository_Cliente(JPA_Cliente jpaCliente) {
        this.jpaCliente = jpaCliente;
    }

    public List<DTO_Cliente> findAll() {
        return jpaCliente.findAll();
    }

    public Optional<DTO_Cliente> findById(UUID id) {
        return jpaCliente.findById(id);
    }

    public DTO_Cliente save(DTO_Cliente cliente) {
        return jpaCliente.save(cliente);
    }

    public void deleteById(UUID id) {
        jpaCliente.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return jpaCliente.existsById(id);
    }
}
