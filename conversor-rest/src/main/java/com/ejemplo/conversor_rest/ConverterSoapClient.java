package com.ejemplo.conversor_rest;

import java.math.BigDecimal;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import jakarta.xml.bind.JAXBElement;

// Importa aquí las clases generadas por xjc (ajusta el paquete):
// import com.ejemplo.calculadora_rest.wsdl.*;

import com.ejemplo.converter.wsdl.*;

/**
 * Cliente SOAP. Por cada operación arma la petición, la envía a server.py
 * y extrae el resultado de la respuesta.
 */
public class ConverterSoapClient extends WebServiceGatewaySupport {

    private final ObjectFactory factory = new ObjectFactory(); // el generado por xjc

    public BigDecimal celsiusToFahrenheit(BigDecimal celsius) {
        CelsiusToFahrenheit req = new CelsiusToFahrenheit();
        req.setCelsius(celsius);
        CelsiusToFahrenheitResponse res = call(factory.createCelsiusToFahrenheit(req),
                "celsius_to_fahrenheit", CelsiusToFahrenheitResponse.class);
        return res.getCelsiusToFahrenheitResult();
    }

    public BigDecimal fahrenheitToCelsius(BigDecimal fahrenheit) {
        FahrenheitToCelsius req = new FahrenheitToCelsius();
        req.setFahrenheit(fahrenheit);
        FahrenheitToCelsiusResponse res = call(factory.createFahrenheitToCelsius(req),
                "fahrenheit_to_celsius", FahrenheitToCelsiusResponse.class);
        return res.getFahrenheitToCelsiusResult();
    }

    public BigDecimal kmToMi(BigDecimal km) {
        KmToMi req = new KmToMi();
        req.setKm(km);
        KmToMiResponse res = call(factory.createKmToMi(req),
                "km_to_mi", KmToMiResponse.class);
        return res.getKmToMiResult();
    }

    @SuppressWarnings("unchecked")
    private <T> T call(JAXBElement<?> request, String soapAction, Class<T> responseType) {
        JAXBElement<T> response = (JAXBElement<T>) getWebServiceTemplate()
                .marshalSendAndReceive(request, new SoapActionCallback(soapAction));
        return response.getValue();
    }
}