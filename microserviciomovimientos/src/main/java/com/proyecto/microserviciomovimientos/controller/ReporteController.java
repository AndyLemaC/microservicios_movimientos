package com.proyecto.microserviciomovimientos.controller;

import com.proyecto.microserviciomovimientos.dto.ReporteDTO;
import com.proyecto.microserviciomovimientos.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> generarReporteEstadoCuenta(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<ReporteDTO> reporte = reporteService.generarReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}
