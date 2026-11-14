package com.ejemplo.conversor_rest;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.soap.client.SoapFaultClientException;

@RestControllerAdvice 
public class ErrorHandler {
    @ExceptionHandler(SoapFaultClientException.class)
    public ResponseEntity<Map<String, String>> soapFault(SoapFaultClientException e){
        // Conserva el motivo del rechazo SOAP y lo expone como error HTTP 400.
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getFaultStringOrReason()));
    }

    @ExceptionHandler(WebServiceIOException.class)
    public ResponseEntity<Map<String, String>> soapCaido(WebServiceIOException e){
        // Indica que la API REST está activa, pero no puede contactar al SOAP.
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of("error", "Servicio SOAP no disponible"));
    }
}

