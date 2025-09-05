package com.proyecto.microserviciomovimientos.service;

import com.proyecto.microserviciomovimientos.dto.MovimientoDTO;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {

    List<MovimientoDTO> findAll();

    MovimientoDTO findById(Long id);

    List<MovimientoDTO> findByNumeroCuenta(String numeroCuenta);

    List<MovimientoDTO> findByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);

    MovimientoDTO save(MovimientoDTO movimientoDTO);

    MovimientoDTO update(Long id, MovimientoDTO movimientoDTO);

    void delete(Long id);
}
