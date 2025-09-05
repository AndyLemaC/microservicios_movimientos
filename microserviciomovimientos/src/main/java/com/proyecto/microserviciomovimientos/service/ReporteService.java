package com.proyecto.microserviciomovimientos.service;

import com.proyecto.microserviciomovimientos.dto.ReporteDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {

    List<ReporteDTO> generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
