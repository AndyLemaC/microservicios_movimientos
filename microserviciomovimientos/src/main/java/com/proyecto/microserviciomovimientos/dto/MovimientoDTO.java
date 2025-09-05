package com.proyecto.microserviciomovimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoDTO {

    private Long id;

    private LocalDate fecha;

    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    private BigDecimal valor;

    private BigDecimal saldo;

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    // Campos adicionales para mostrar en reportes
    private String tipoCuenta;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private String nombreCliente;
}
