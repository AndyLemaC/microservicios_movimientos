
package com.proyecto.microserviciomovimientos.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.microserviciomovimientos.dto.CuentaDTO;
import com.proyecto.microserviciomovimientos.dto.MovimientoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovimientoFlowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void retiroSinSaldoDebeFallar_conMensajeSaldoNoDisponible() throws Exception {
        // 1) Crear cuenta con saldo 100
        CuentaDTO cuenta = new CuentaDTO();
        cuenta.setNumeroCuenta("225487");
        cuenta.setTipoCuenta("Corriente");
        cuenta.setSaldoInicial(new BigDecimal("100"));
        cuenta.setEstado(true);
        cuenta.setClienteId(2L);

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated());

        // 2) Intentar retiro de 600 -> debe fallar
        MovimientoDTO mov = new MovimientoDTO();
        mov.setNumeroCuenta("225487");
        mov.setTipoMovimiento("Retiro");
        mov.setValor(new BigDecimal("-600"));

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mov)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Saldo no disponible"));
    }
}
