package com.proyecto.microserviciomovimientos.controller;

import com.proyecto.microserviciomovimientos.dto.MovimientoDTO;
import com.proyecto.microserviciomovimientos.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping
   public ResponseEntity<List<MovimientoDTO>> getAllMovimientos() {
        List<MovimientoDTO> movimientos = movimientoService.findAll();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> getMovimientoById(@PathVariable Long id) {
        MovimientoDTO movimiento = movimientoService.findById(id);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
   public ResponseEntity<List<MovimientoDTO>> getMovimientosByCuenta(@PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.findByNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/cliente/{clienteId}")
   public ResponseEntity<List<MovimientoDTO>> getMovimientosByClienteAndFechas(
            @PathVariable Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<MovimientoDTO> movimientos = movimientoService.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> createMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO nuevoMovimiento = movimientoService.save(movimientoDTO);
        return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> updateMovimiento(@PathVariable Long id,
                                                          @Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO updatedMovimiento = movimientoService.update(id, movimientoDTO);
        return ResponseEntity.ok(updatedMovimiento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimiento(@PathVariable Long id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
