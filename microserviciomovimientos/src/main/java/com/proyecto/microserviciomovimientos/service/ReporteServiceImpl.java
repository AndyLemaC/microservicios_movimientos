package com.proyecto.microserviciomovimientos.service;


import com.proyecto.microserviciomovimientos.dto.ReporteDTO;
import com.proyecto.microserviciomovimientos.entities.Cuenta;
import com.proyecto.microserviciomovimientos.entities.Movimiento;
import com.proyecto.microserviciomovimientos.exceptions.ResourceNotFoundException;
import com.proyecto.microserviciomovimientos.message.ClienteProducer;
import com.proyecto.microserviciomovimientos.repository.CuentaRepository;
import com.proyecto.microserviciomovimientos.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteProducer clienteProducer;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte de estado de cuenta para cliente ID: {} entre fechas: {} y {}", clienteId, fechaInicio, fechaFin);

        // Verificar si el cliente existe
        boolean clienteExiste = clienteProducer.verificarCliente(clienteId);
        if (!clienteExiste) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId);
        }

        // Obtener cuentas del cliente
        List<Cuenta> cuentasCliente = cuentaRepository.findByClienteId(clienteId);
        if (cuentasCliente.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron cuentas para el cliente con ID: " + clienteId);
        }

//        // Obtener el nombre del cliente
        String nombreCliente = clienteProducer.obtenerNombreCliente(clienteId);

        // Obtener movimientos en el rango de fechas
        List<Movimiento> movimientos = movimientoRepository.findByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin);

        // Mapear movimientos a DTOs de reporte
        return movimientos.stream()
                .map(movimiento -> mapToReporteDTO(movimiento, nombreCliente))
                .collect(Collectors.toList());
    }

    private ReporteDTO mapToReporteDTO(Movimiento movimiento, String nombreCliente) {
        // Obtenemos la cuenta asociada
        Cuenta cuenta = movimiento.getCuenta();

        return ReporteDTO.builder()
                .fecha(movimiento.getFecha())
                .nombreCliente(nombreCliente)
                .numeroCuenta(movimiento.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .movimiento(movimiento.getValor())
                .saldoDisponible(movimiento.getSaldo())
                .build();
    }
}