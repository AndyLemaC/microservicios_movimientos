package com.proyecto.microservicioclientes.service;

import com.proyecto.microservicioclientes.dto.ClienteDTO;
import com.proyecto.microservicioclientes.dto.ClienteResponseDTO;
import com.proyecto.microservicioclientes.entities.Cliente;
import com.proyecto.microservicioclientes.exceptions.ClienteNotFoundException;
import com.proyecto.microservicioclientes.exceptions.DuplicateIdentificacionException;
import com.proyecto.microservicioclientes.message.ClienteProducer;
import com.proyecto.microservicioclientes.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ClienteProducer clienteProducer;

    public List<ClienteResponseDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));
        return mapToResponseDTO(cliente);
    }

    public ClienteResponseDTO getClienteByIdentificacion(String identificacion) {
        Cliente cliente = clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con identificación: " + identificacion));
        return mapToResponseDTO(cliente);
    }


    @Transactional
    public ClienteResponseDTO createCliente(ClienteDTO clienteDTO) {
        if (clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new DuplicateIdentificacionException("Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }

        Cliente cliente = mapToEntity(clienteDTO);
        Cliente savedCliente = clienteRepository.save(cliente);

        // Notificar al microservicio de cuentas sobre el nuevo cliente
        clienteProducer.sendClienteCreatedMessage(savedCliente);

        return mapToResponseDTO(savedCliente);
    }

    @Transactional
    public ClienteResponseDTO updateCliente(Long id, ClienteDTO clienteDTO) {
        Cliente existingCliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente no encontrado con ID: " + id));

        // Verificar identificación duplicada solo si ha cambiado
        if (!existingCliente.getIdentificacion().equals(clienteDTO.getIdentificacion()) &&
                clienteRepository.existsByIdentificacion(clienteDTO.getIdentificacion())) {
            throw new DuplicateIdentificacionException("Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }

        // Actualizar propiedades
        existingCliente.setNombre(clienteDTO.getNombre());
        existingCliente.setGenero(clienteDTO.getGenero());
        existingCliente.setEdad(clienteDTO.getEdad());
        existingCliente.setIdentificacion(clienteDTO.getIdentificacion());
        existingCliente.setDireccion(clienteDTO.getDireccion());
        existingCliente.setTelefono(clienteDTO.getTelefono());
        existingCliente.setContrasenia(clienteDTO.getContrasena());
        existingCliente.setEstado(clienteDTO.getEstado());

        Cliente updatedCliente = clienteRepository.save(existingCliente);

        // Notificar al microservicio de cuentas sobre la actualización del cliente
        clienteProducer.sendClienteUpdatedMessage(updatedCliente);

        return mapToResponseDTO(updatedCliente);
    }

    @Transactional
    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente no encontrado con ID: " + id);
        }

        clienteRepository.deleteById(id);

        // Notificar al microservicio de cuentas sobre la eliminación del cliente
        clienteProducer.sendClienteDeletedMessage(id);
    }


    public boolean existeCliente(Long clienteId) {
        return clienteRepository.existsById(clienteId);
    }

    public String obtenerNombreCliente(Long clienteId) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        return cliente.map(Cliente::getNombre).orElse("Cliente " + clienteId);
    }

    private ClienteResponseDTO mapToResponseDTO(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setClienteId(cliente.getClienteId());
        dto.setNombre(cliente.getNombre());
        dto.setGenero(cliente.getGenero());
        dto.setEdad(cliente.getEdad());
        dto.setIdentificacion(cliente.getIdentificacion());
        dto.setDireccion(cliente.getDireccion());
        dto.setTelefono(cliente.getTelefono());
        dto.setEstado(cliente.getEstado());
        return dto;
    }

    private Cliente mapToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setContrasenia(dto.getContrasena());
        cliente.setEstado(dto.getEstado());
        return cliente;
    }
}
