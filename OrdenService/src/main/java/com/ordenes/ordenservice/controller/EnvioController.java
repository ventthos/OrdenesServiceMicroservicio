package com.ordenes.ordenservice.controller;

import com.ordenes.ordenservice.models.EnvioProgramado;
import com.ordenes.ordenservice.response.GeneralResponse;
import com.ordenes.ordenservice.service.GetEnviosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final GetEnviosService getEnviosService;

    @GetMapping
    public ResponseEntity<GeneralResponse<List<EnvioProgramado>>> getEnvios() {
        List<EnvioProgramado> envios = getEnviosService.execute();
        return ResponseEntity.ok(GeneralResponse.<List<EnvioProgramado>>builder()
                .status("SUCCESS")
                .message("Envíos programados recuperados de PostgreSQL")
                .data(envios)
                .build());
    }
}
