package com.proyecto.microserviciomovimientos.repository;

import  com.proyecto.microserviciomovimientos.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    List<Cuenta> findByClienteId(Long clienteId);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    boolean existsByClienteId(Long clienteId);
}
