package com.ordenes.ordenservice.repository;

import com.ordenes.ordenservice.models.EnvioProgramado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioProgramadoRepository extends JpaRepository<EnvioProgramado, Long> {
}
