
package com.proyecto.microservicioclientes.service;

import com.proyecto.microservicioclientes.dto.ClienteDTO;
import com.proyecto.microservicioclientes.dto.ClienteResponseDTO;
import com.proyecto.microservicioclientes.entities.Cliente;
import com.proyecto.microservicioclientes.exceptions.DuplicateIdentificacionException;
import com.proyecto.microservicioclientes.message.ClienteProducer;
import com.proyecto.microservicioclientes.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ClienteProducer clienteProducer;

    @InjectMocks
    ClienteService clienteService;

    @Test
    void createCliente_ok_publishesEvent() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Jose Lema");
        dto.setGenero("M");
        dto.setEdad(30);
        dto.setIdentificacion("0102030405");
        dto.setDireccion("Otavalo sn y principal");
        dto.setTelefono("098254785");
        dto.setContrasena("1234");
        dto.setEstado(true);

        when(clienteRepository.existsByIdentificacion("0102030405")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setClienteId(1L);
            return c;
        });

        ClienteResponseDTO resp = clienteService.createCliente(dto);
        assertNotNull(resp);
        assertEquals(1L, resp.getClienteId());
        assertEquals("Jose Lema", resp.getNombre());

        ArgumentCaptor<Cliente> ev = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteProducer, times(1)).sendClienteCreatedMessage(ev.capture());
        assertEquals("0102030405", ev.getValue().getIdentificacion());
    }

    @Test
    void createCliente_duplicateIdentification_throws() {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdentificacion("0102030405");
        when(clienteRepository.existsByIdentificacion("0102030405")).thenReturn(true);
        assertThrows(DuplicateIdentificacionException.class, () -> clienteService.createCliente(dto));
        verifyNoInteractions(clienteProducer);
    }
}
