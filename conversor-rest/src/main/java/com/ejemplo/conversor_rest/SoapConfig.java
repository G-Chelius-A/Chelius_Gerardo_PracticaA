package com.ejemplo.conversor_rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration 
public class SoapConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        //Paquete donde el plugin genera las clases a partir del WSDL
        marshaller.setContextPath("com.ejemplo.converter.wsdl");
        return marshaller;
    }

    @Bean
    public ConverterSoapClient converterSoapClient(Jaxb2Marshaller marshaller,
        @Value("${converter.soap.url}") String url) {
            ConverterSoapClient client = new ConverterSoapClient();
            client.setDefaultUri(url);
            client.setMarshaller(marshaller);
            client.setUnmarshaller(marshaller);
            return client;
        }
}
