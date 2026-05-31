package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.EnvioProgramado;
import com.ordenes.ordenservice.repository.EnvioProgramadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetEnviosService {
    private final EnvioProgramadoRepository envioProgramadoRepository;

    public List<EnvioProgramado> execute() {
        log.info("Obteniendo todos los envíos programados de PostgreSQL");
        return envioProgramadoRepository.findAll();
    }
}
