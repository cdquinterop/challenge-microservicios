package com.example.personaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClienteServiceException extends RuntimeException {

    public static final String CLIENTE_NOT_FOUND = "Cliente no encontrado.";
    public static final String CLIENTE_CREATION_ERROR = "Error al crear el cliente.";
    public static final String CLIENTE_UPDATE_ERROR = "Error al actualizar el cliente.";
    public static final String CLIENTE_DELETION_ERROR = "Error al eliminar el cliente.";

    public ClienteServiceException(String message) {
        super(message);
    }

    public ClienteServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
