package com.proyecto.microserviciomovimientos.controller;

import com.proyecto.microserviciomovimientos.dto.CuentaDTO;
import com.proyecto.microserviciomovimientos.service.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Slf4j
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> getAllCuentas() {
        List<CuentaDTO> cuentas = cuentaService.findAll();
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> getCuentaByNumeroCuenta(@PathVariable String numeroCuenta) {
        CuentaDTO cuenta = cuentaService.findByNumeroCuenta(numeroCuenta);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaDTO>> getCuentasByClienteId(@PathVariable Long clienteId) {
        List<CuentaDTO> cuentas = cuentaService.findByClienteId(clienteId);
        return ResponseEntity.ok(cuentas);
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> createCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaService.save(cuentaDTO);
        return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
    }

    @PutMapping("/{numeroCuenta}")
   public ResponseEntity<CuentaDTO> updateCuenta(@PathVariable String numeroCuenta,
                                                  @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO updatedCuenta = cuentaService.update(numeroCuenta, cuentaDTO);
        return ResponseEntity.ok(updatedCuenta);
    }

   public ResponseEntity<Void> deleteCuenta(@PathVariable String numeroCuenta) {
        cuentaService.delete(numeroCuenta);
        return ResponseEntity.noContent().build();
    }
}
