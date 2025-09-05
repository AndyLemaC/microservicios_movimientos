package com.proyecto.microserviciomovimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    private Long id;
    private String nombre;
    private String identificacion;
    private String direccion;
    private String telefono;
    private String contrasenia;
    private Boolean estado;
}
