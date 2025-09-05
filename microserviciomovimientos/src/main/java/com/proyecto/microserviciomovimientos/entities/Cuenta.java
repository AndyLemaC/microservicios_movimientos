package com.proyecto.microserviciomovimientos.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    @Id
    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Column(name = "tipo_cuenta", nullable = false)
    private String tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @Column(name = "saldo_inicial", nullable = false)
    private BigDecimal saldoInicial;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", nullable = false)
    private Boolean estado;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    // Relación con Movimiento
    @OneToMany(mappedBy = "cuenta", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimiento> movimientos;
}