package com.ejemplo.conversor_rest;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversor")
public class ConversorController {
    private final ConverterSoapClient soap;

    public ConversorController(ConverterSoapClient soap) {
        this.soap = soap;
    }

    @GetMapping("/celsius-to-fahrenheit")
    public Map<String, Object> celsiusToFahrenheit(@RequestParam BigDecimal celsius) {
        return resultado("celsius_to_fahrenheit", "celsius", celsius,
                soap.celsiusToFahrenheit(celsius));
    }

    @GetMapping("/fahrenheit-to-celsius")
    public Map<String, Object> fahrenheitToCelsius(@RequestParam BigDecimal fahrenheit) {
        return resultado("fahrenheit_to_celsius", "fahrenheit", fahrenheit,
                soap.fahrenheitToCelsius(fahrenheit));
    }

    @GetMapping("/km-to-mi")
    public Map<String, Object> kmToMi(@RequestParam BigDecimal km) {
        return resultado("km_to_mi", "km", km, soap.kmToMi(km));
    }

    private Map<String, Object> resultado(String operacion, String parametro,
            BigDecimal valor, BigDecimal resultado) {
        return Map.of("operacion", operacion, parametro, valor, "resultado", resultado);
    }
}
