package com.proyecto.microserviciomovimientos.service;

import com.proyecto.microserviciomovimientos.dto.MovimientoDTO;
import com.proyecto.microserviciomovimientos.entities.Cuenta;
import com.proyecto.microserviciomovimientos.entities.Movimiento;
import com.proyecto.microserviciomovimientos.exceptions.ResourceNotFoundException;
import com.proyecto.microserviciomovimientos.exceptions.SaldoNoDisponibleException;
import com.proyecto.microserviciomovimientos.repository.CuentaRepository;
import com.proyecto.microserviciomovimientos.repository.MovimientoRepository;
import com.proyecto.microserviciomovimientos.message.ClienteProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteProducer clienteProducer;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> findAll() {
        log.info("Buscando todos los movimientos");
        return movimientoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoDTO findById(Long id) {
        log.info("Buscando movimiento por ID: {}", id);
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con ID: " + id));

        return mapToDTO(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> findByNumeroCuenta(String numeroCuenta) {
        log.info("Buscando movimientos por número de cuenta: {}", numeroCuenta);
        return movimientoRepository.findByNumeroCuenta(numeroCuenta)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoDTO> findByClienteIdAndFechaBetween(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Buscando movimientos por cliente ID: {} entre fechas: {} y {}", clienteId, fechaInicio, fechaFin);
        return movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovimientoDTO save(MovimientoDTO movimientoDTO) {
        log.info("Creando nuevo movimiento: {}", movimientoDTO);

        // Buscar la cuenta
        Cuenta cuenta = cuentaRepository.findById(movimientoDTO.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + movimientoDTO.getNumeroCuenta()));

        // Determinar tipo de movimiento
        String tipoMovimiento = movimientoDTO.getValor().compareTo(BigDecimal.ZERO) > 0 ? "Depósito" : "Retiro";

        // Calcular nuevo saldo
        BigDecimal saldoActual = obtenerSaldoActual(cuenta.getNumeroCuenta());
        BigDecimal nuevoSaldo = saldoActual.add(movimientoDTO.getValor());

        // Verificar si hay saldo suficiente para retiros
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no disponible para realizar el retiro");
        }

        // Crear y guardar el movimiento
        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDate.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(movimientoDTO.getValor())
                .saldo(nuevoSaldo)
                .numeroCuenta(movimientoDTO.getNumeroCuenta())
                .build();

        Movimiento savedMovimiento = movimientoRepository.save(movimiento);
        log.info("Movimiento guardado con éxito: {}", savedMovimiento);

        return mapToDTO(savedMovimiento);
    }

    @Override
    @Transactional
    public MovimientoDTO update(Long id, MovimientoDTO movimientoDTO) {
        log.info("Actualizando movimiento con ID: {}", id);

        // Por seguridad, no se deberían actualizar movimientos ya realizados
        // Sin embargo, implementamos la funcionalidad con restricciones

        Movimiento existingMovimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con ID: " + id));

        // Solo permitimos actualizar la fecha para correcciones administrativas
        existingMovimiento.setFecha(movimientoDTO.getFecha());

        Movimiento updatedMovimiento = movimientoRepository.save(existingMovimiento);
        log.info("Movimiento actualizado con éxito: {}", updatedMovimiento);

        return mapToDTO(updatedMovimiento);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Eliminando movimiento con ID: {}", id);

        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento no encontrado con ID: " + id);
        }

        // Por seguridad, no deberíamos permitir eliminar movimientos en un sistema real
        // Esta implementación es para propósitos de la prueba técnica

        movimientoRepository.deleteById(id);
        log.info("Movimiento eliminado con éxito: {}", id);
    }

    // Método para obtener el saldo actual de una cuenta
    private BigDecimal obtenerSaldoActual(String numeroCuenta) {
        // Obtener la cuenta
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));

        // Obtener el último movimiento si existe
        List<Movimiento> movimientos = movimientoRepository.findByNumeroCuenta(numeroCuenta);

        if (movimientos.isEmpty()) {
            // Si no hay movimientos, el saldo es el inicial
            return cuenta.getSaldoInicial();
        } else {
            // Ordenar movimientos por ID (asumiendo que el ID es secuencial)
            movimientos.sort((m1, m2) -> m2.getId().compareTo(m1.getId()));

            // Retornar el saldo del último movimiento
            return movimientos.get(0).getSaldo();
        }
    }

    // Métodos auxiliares para mapeo de entidades
    private MovimientoDTO mapToDTO(Movimiento movimiento) {
        MovimientoDTO dto = MovimientoDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .numeroCuenta(movimiento.getNumeroCuenta())
                .build();

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(movimiento.getNumeroCuenta())
                .orElse(null);

        if (cuenta != null) {
            dto.setTipoCuenta(cuenta.getTipoCuenta());
            dto.setSaldoInicial(cuenta.getSaldoInicial());
            dto.setEstado(cuenta.getEstado());
            try {
                String nombre = clienteProducer.obtenerNombreCliente(cuenta.getClienteId());
                dto.setNombreCliente(nombre);
            } catch (Exception e) {
                log.warn("No se pudo obtener nombre del cliente por RPC (clienteId={}): {}",
                        cuenta.getClienteId(), e.getMessage());
            }
        }

        return dto;
    }
}