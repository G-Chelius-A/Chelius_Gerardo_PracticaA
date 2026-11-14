from decimal import Decimal as PyDecimal
from wsgiref.simple_server import make_server

from spyne import Application, Decimal, ServiceBase, rpc
from spyne.model.fault import Fault
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication


class ConverterService(ServiceBase):

    @rpc(Decimal, _returns=Decimal)
    def celsius_to_fahrenheit(ctx, celsius):
        # Rechaza temperaturas inferiores al límite físico del cero absoluto.
        if celsius < PyDecimal("-273.15"):
            raise Fault(
                faultcode="Client.Below_Zero",
                faultstring=(
                    "La temperatura es menor al cero absoluto (-273.15°C)"
                ),
            )

        fahrenheit = celsius * PyDecimal("1.8") + PyDecimal("32")
        return fahrenheit

    @rpc(Decimal, _returns=Decimal)
    def fahrenheit_to_celsius(ctx, fahrenheit):
        # Aplica el mismo límite físico, expresado en grados Fahrenheit.
        if fahrenheit < PyDecimal("-459.67"):
            raise Fault(
                faultcode="Client.Below_Zero",
                faultstring=(
                    "La temperatura es menor al cero absoluto (-459.67°F)"
                ),
            )

        celsius = (fahrenheit - PyDecimal("32")) / PyDecimal("1.8")
        return celsius

    @rpc(Decimal, _returns=Decimal)
    def km_to_mi(ctx, km):
        # Una distancia negativa no es válida para esta conversión.
        if km < PyDecimal("0"):
            raise Fault(
                faultcode="Client.Negative_Distance",
                faultstring="La distancia no puede ser negativa",
            )

        mi = km * PyDecimal("0.621371")
        return mi


application = Application(
    [ConverterService],
    "spyne.converter.soap",
    in_protocol=Soap11(validator="lxml"),
    out_protocol=Soap11(),
)

wsgi_application = WsgiApplication(application)

if __name__ == "__main__":
    server = make_server("0.0.0.0", 8000, wsgi_application)
    print("El servidor SOAP está escuchando en: http://127.0.0.1:8000")
    print("WSDL ejecutándose en: http://127.0.0.1:8000/?wsdl")
    server.serve_forever()
