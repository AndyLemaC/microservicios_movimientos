package com.proyecto.microserviciomovimientos.service;

import com.proyecto.microserviciomovimientos.dto.CuentaDTO;

import java.util.List;

public interface CuentaService {

    List<CuentaDTO> findAll();

    CuentaDTO findByNumeroCuenta(String numeroCuenta);

    List<CuentaDTO> findByClienteId(Long clienteId);

    CuentaDTO save(CuentaDTO cuentaDTO);

    CuentaDTO update(String numeroCuenta, CuentaDTO cuentaDTO);

    void delete(String numeroCuenta);

    boolean existsByClienteId(Long clienteId);
}
