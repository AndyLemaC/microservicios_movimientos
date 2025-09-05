package com.proyecto.microservicioclientes.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent<T> implements Serializable {
    private String eventType;
    private T payload;
}
