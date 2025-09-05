package com.proyecto.microserviciomovimientos.service;

import com.proyecto.microserviciomovimientos.dto.CuentaDTO;
import com.proyecto.microserviciomovimientos.entities.Cuenta;
import com.proyecto.microserviciomovimientos.exceptions.ResourceNotFoundException;
import com.proyecto.microserviciomovimientos.message.ClienteProducer;
import com.proyecto.microserviciomovimientos.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final ClienteProducer clienteProducer;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> findAll() {
        log.info("Buscando todas las cuentas");
        return cuentaRepository.findAll()
                .stream()
                .map(cuenta -> {
                    String nombreCliente = clienteProducer.obtenerNombreCliente(cuenta.getClienteId());
                    return mapToDTO(cuenta, nombreCliente);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO findByNumeroCuenta(String numeroCuenta) {
        log.info("Buscando cuenta por número: {}", numeroCuenta);
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con número: " + numeroCuenta));

        // Obtener el nombre del cliente usando RabbitMQ
        String nombreCliente = clienteProducer.obtenerNombreCliente(cuenta.getClienteId());

        return mapToDTO(cuenta, nombreCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> findByClienteId(Long clienteId) {
        log.info("Buscando cuentas por cliente ID: {}", clienteId);

        // Obtener el nombre del cliente usando RabbitMQ
        String nombreCliente = clienteProducer.obtenerNombreCliente(clienteId);

        return cuentaRepository.findByClienteId(clienteId)
                .stream()
                .map(cuenta -> mapToDTO(cuenta, nombreCliente)) // Pasar el nombre del cliente
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaDTO save(CuentaDTO cuentaDTO) {
        log.info("Creando nueva cuenta: {}", cuentaDTO);

        // Verificar si existe el cliente
        if (!clienteProducer.verificarCliente(cuentaDTO.getClienteId())) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + cuentaDTO.getClienteId());
        }

        Cuenta cuenta = mapToEntity(cuentaDTO);
        Cuenta savedCuenta = cuentaRepository.save(cuenta);

        log.info("Cuenta creada con éxito: {}", savedCuenta);
        return mapToDTO(savedCuenta, clienteProducer.obtenerNombreCliente(cuentaDTO.getClienteId()));
    }

    @Override
    @Transactional
    public CuentaDTO update(String numeroCuenta, CuentaDTO cuentaDTO) {
        log.info("Actualizando cuenta: {}", numeroCuenta);

        Cuenta existingCuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con numero: " + numeroCuenta));

        existingCuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        existingCuenta.setEstado(cuentaDTO.getEstado());
        // No actualizamos el saldo inicial ni el cliente por seguridad

        Cuenta updatedCuenta = cuentaRepository.save(existingCuenta);
        log.info("Cuenta actualizada con éxito: {}", updatedCuenta);

        return mapToDTO(updatedCuenta, clienteProducer.obtenerNombreCliente(existingCuenta.getClienteId()));
    }

    @Override
    @Transactional
    public void delete(String numeroCuenta) {
        log.info("Eliminando cuenta: {}", numeroCuenta);

        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con numero: " + numeroCuenta));

        // Verificar si hay movimientos asociados
        if (!cuenta.getMovimientos().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar la cuenta porque tiene movimientos asociados.");
        }

        cuentaRepository.deleteById(numeroCuenta);
        log.info("Cuenta eliminada con éxito: {}", numeroCuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClienteId(Long clienteId) {
        return cuentaRepository.existsByClienteId(clienteId);
    }

    // Métodos auxiliares para mapeo de entidades
    private CuentaDTO mapToDTO(Cuenta cuenta, String nombreCliente) {
        return CuentaDTO.builder()
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .nombreCliente(nombreCliente) // Agregar el nombre del cliente
                .build();
    }

    private Cuenta mapToEntity(CuentaDTO cuentaDTO) {
        return Cuenta.builder()
                .numeroCuenta(cuentaDTO.getNumeroCuenta())
                .tipoCuenta(cuentaDTO.getTipoCuenta())
                .saldoInicial(cuentaDTO.getSaldoInicial())
                .estado(cuentaDTO.getEstado())
                .clienteId(cuentaDTO.getClienteId())
                .build();
    }
}