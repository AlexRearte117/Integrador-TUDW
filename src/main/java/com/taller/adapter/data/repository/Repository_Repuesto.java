package com.taller.adapter.data.repository;

import com.taller.adapter.data.entity.DTO_Repuesto;
import com.taller.adapter.data.jpa.JPA_OrdenRepuesto;
import com.taller.adapter.data.jpa.JPA_Repuesto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class Repository_Repuesto {

    private final JPA_Repuesto jpaRepuesto;
    private final JPA_OrdenRepuesto jpaOrdenRepuesto;

    public Repository_Repuesto(JPA_Repuesto jpaRepuesto, JPA_OrdenRepuesto jpaOrdenRepuesto) {
        this.jpaRepuesto = jpaRepuesto;
        this.jpaOrdenRepuesto = jpaOrdenRepuesto;
    }

    public List<DTO_Repuesto> findAll() {
        return jpaRepuesto.findAll();
    }

    public Optional<DTO_Repuesto> findById(UUID id) {
        return jpaRepuesto.findById(id);
    }

    public DTO_Repuesto save(DTO_Repuesto repuesto) {
        return jpaRepuesto.save(repuesto);
    }

    public void deleteById(UUID id) {
        jpaRepuesto.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return jpaRepuesto.existsById(id);
    }

    public boolean estaUsadoEnOrdenes(UUID id) {
        return jpaOrdenRepuesto.existsByRepuestoId(id);
    }
}
