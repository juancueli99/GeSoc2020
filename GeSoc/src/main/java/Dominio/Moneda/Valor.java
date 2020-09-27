package Dominio.Moneda;

import API.ML.ControllerMercadoLibre;
import API.ML.DTOs.ConversionDTO;
import API.ML.DTOs.MonedaDTO;
import API.ML.Excepciones.ExcepcionNoSePudoConvertir;
import API.ML.Excepciones.NoExisteMonedaException;

import javax.persistence.*;
import java.io.IOException;
import java.math.BigDecimal;

@Entity
@Table(name = "mon_valor")
public class Valor {
    @Id
    @GeneratedValue
    private int moneda;

    @Column(name = "nombre")
    private String tipoDeMoneda;

    @Column(name = "simbolo")
    private String simbolo;

    @Column(name = "pais")
    private String pais;

    @Column(name = "importe")
    private double importe;

    public Valor(String pais, double importe) {

        ControllerMercadoLibre controller;
        controller = ControllerMercadoLibre.getControllerMercadoLibre();

        String nombreMoneda = controller.getPais(pais).getCurrency_id();
        MonedaDTO monedaDTO = controller.getMonedaByID(nombreMoneda);

        BigDecimal importeNuevo = new BigDecimal(importe);

        this.simbolo = monedaDTO.getSymbol();
        this.tipoDeMoneda = monedaDTO.getDescription();
        this.importe = importeNuevo.setScale(monedaDTO.getDecimal_places()).doubleValue();
        this.pais = pais;
    }

    public String getTipoDeMoneda() {
        return tipoDeMoneda;
    }

    public String getPais() {
        return pais;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public double getImporte() {
        return importe;
    }

    public double convertirA(String unPais){

        ControllerMercadoLibre controller;
        controller = ControllerMercadoLibre.getControllerMercadoLibre();

        String nombreMoneda = controller.getPais(unPais).getCurrency_id();
        MonedaDTO monedaDTO = controller.getMonedaByID(nombreMoneda);

        double monedaConvertida = 0;
        try {
            ConversionDTO conversionDTO = controller.convertirMoneda(this.tipoDeMoneda,monedaDTO.getDescription());
            monedaConvertida =  this.importe * conversionDTO.getRatio();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExcepcionNoSePudoConvertir excepcionNoSePudoConvertir) {
            excepcionNoSePudoConvertir.printStackTrace();
        } catch (NoExisteMonedaException e) {
            e.printStackTrace();
        }

        BigDecimal importeNuevo = new BigDecimal(monedaConvertida);
        return importeNuevo.setScale(monedaDTO.getDecimal_places()).doubleValue();
    }

}
