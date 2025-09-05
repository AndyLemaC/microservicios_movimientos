package com.proyecto.microserviciomovimientos.entities;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @NotNull(message = "El saldo es obligatorio")
    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @NotNull(message = "El número de cuenta es obligatorio")
    @Column(name = "numero_cuenta", nullable = false)
    private String numeroCuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "numero_cuenta", referencedColumnName = "numero_cuenta", insertable = false, updatable = false)
    private Cuenta cuenta;
}
