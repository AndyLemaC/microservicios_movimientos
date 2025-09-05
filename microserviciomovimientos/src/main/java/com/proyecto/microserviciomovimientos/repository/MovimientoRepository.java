package com.proyecto.microserviciomovimientos.repository;

import com.proyecto.microserviciomovimientos.entities.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByNumeroCuenta(String numeroCuenta);

    List<Movimiento> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT m FROM Movimiento m JOIN FETCH m.cuenta c WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin AND c.clienteId = :clienteId ORDER BY m.fecha DESC")
    List<Movimiento> findByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}