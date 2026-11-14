# PRÁCTICA A - CONVERSOR DE UNIDADES - UNIDAD 4 (SOAP) - SOA
----
Por: Gerardo Yeray Chelius Altamirano | 7°B Ingeniería en Sistemas Computacionales | UCC | 2026
---
Este proyecto contiene dos servicios relacionados para convertir temperaturas y
distancias:

- `conversor-soap`: servidor SOAP implementado en Python con Spyne.
- `conversor-rest`: API REST implementada con Spring Boot. Actúa como cliente
  SOAP y delega las conversiones en `conversor-soap`.

## Requisitos

- Java 21 para la API REST.
- Python 3.10 o posterior para el servidor SOAP.
- Conexión local disponible en los puertos `8000` (SOAP) y `8080` (REST).

## Instalación y ejecución

### 1. Iniciar el servidor SOAP

En una terminal, desde la raíz del proyecto:

```powershell
cd conversor-soap
python -m venv venv
.\venv\Scripts\activate
python -m pip install spyne lxml
python Server.py
```

El servicio SOAP escucha en `http://127.0.0.1:8000/` y publica el WSDL en
`http://127.0.0.1:8000/?wsdl`. Mantén esta terminal abierta.

### 2. Iniciar la API REST

En otra terminal, desde la raíz del proyecto:

```powershell
cd conversor-rest
.\mvnw.cmd spring-boot:run
```

La API REST estará disponible en `http://localhost:8080`. La URL del servidor
SOAP se configura mediante `converter.soap.url` en
`conversor-rest/src/main/resources/application.properties`.

## Operaciones disponibles

| Conversión | Método y ruta REST | Parámetro |
|---|---|---|
| Celsius a Fahrenheit | `GET /api/conversor/celsius-to-fahrenheit` | `celsius` |
| Fahrenheit a Celsius | `GET /api/conversor/fahrenheit-to-celsius` | `fahrenheit` |
| Kilómetros a millas | `GET /api/conversor/km-to-mi` | `km` |

Ejemplo:

```text
http://localhost:8080/api/conversor/celsius-to-fahrenheit?celsius=25
```

La API responde con JSON que identifica la operación, el valor de entrada y el
resultado. Si el servidor SOAP rechaza el valor (por debajo del cero absoluto
o una distancia negativa), REST responde con `400 Bad Request`. Si el servidor
SOAP no está disponible, responde con `503 Service Unavailable`.

## Pruebas manuales

Con ambos servicios activos, abrir las URL siguientes en el navegador o desde Postman.

| Caso | Solicitud | Resultado esperado |
|---|---|---|
| Celsius a Fahrenheit | `http://localhost:8080/api/conversor/celsius-to-fahrenheit?celsius=100` | `200 OK`, resultado `212` |
| Fahrenheit a Celsius | `http://localhost:8080/api/conversor/fahrenheit-to-celsius?fahrenheit=32` | `200 OK`, resultado `0` |
| Kilómetros a millas | `http://localhost:8080/api/conversor/km-to-mi?km=10` | `200 OK`, resultado `6.21371` (6,21371 con separador decimal español) |
| Temperatura menor al cero absoluto | `http://localhost:8080/api/conversor/celsius-to-fahrenheit?celsius=-300` | `400 Bad Request` y JSON con el error SOAP |
| Distancia negativa | `http://localhost:8080/api/conversor/km-to-mi?km=-1` | `400 Bad Request` y JSON con el error SOAP |

Para comprobar que REST responde controladamente cuando SOAP no está disponible:

1. Detener el servidor SOAP con `Ctrl+C` y deja la API REST ejecutándose.
2. Solicitar cualquier conversión, por ejemplo:

   ```
   http://localhost:8080/api/conversor/celsius-to-fahrenheit?celsius=100
   ```

3. La respuesta esperada es `503 Service Unavailable` con un JSON que indica
   que el servicio SOAP no está disponible. Volver a iniciar SOAP para continuar
   probando las demás operaciones.

## Estructura

```text
.
├── conversor-rest/
│   ├── .mvn/wrapper/        # Archivos del Maven Wrapper
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ejemplo/conversor_rest/
│   │   │   │   ├── ConversorRestApplication.java
│   │   │   │   ├── ConversorController.java
│   │   │   │   ├── ConverterSoapClient.java
│   │   │   │   ├── ErrorHandler.java
│   │   │   │   └── SoapConfig.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── wsdl/
│   │   │           ├── converter.wsdl
│   │   │           └── bindings.xjb
│   │   └── test/java/com/ejemplo/conversor_rest/
│   │       └── ConversorRestApplicationTests.java
│   ├── mvnw, mvnw.cmd      # Maven Wrapper para Linux/macOS y Windows
│   └── pom.xml
├── conversor-soap/
│   └── Server.py            # Servicio SOAP
├── .gitignore
└── README.md
```

### Código Java

- `ConversorRestApplication.java`: punto de entrada que inicia Spring Boot.
- `ConversorController.java`: publica las tres rutas REST, recibe los valores
  decimales y construye la respuesta JSON.
- `ConverterSoapClient.java`: crea los mensajes SOAP, invoca las operaciones
  correspondientes y extrae sus resultados.
- `SoapConfig.java`: configura JAXB para serializar y deserializar mensajes, y
  establece la URL del servicio SOAP desde `application.properties`.
- `ErrorHandler.java`: convierte los errores SOAP en respuestas HTTP
  controladas (`400` para fallos de validación y `503` si SOAP no está
  disponible).
- `ConversorRestApplicationTests.java`: comprueba que el contexto de Spring
  Boot pueda iniciar.

### Recursos de la aplicación REST

- `application.properties`: define el puerto HTTP (`8080`) y la URL del
  servicio SOAP (`converter.soap.url`).
- `wsdl/converter.wsdl`: contrato que describe las operaciones y mensajes SOAP
  del conversor.
- `wsdl/bindings.xjb`: ajustes JAXB usados al generar las clases Java a partir
  del WSDL.

Las clases JAXB generadas por Maven se guardan bajo `target/`; no se editan
manualmente ni se incluyen en el control de versiones.
