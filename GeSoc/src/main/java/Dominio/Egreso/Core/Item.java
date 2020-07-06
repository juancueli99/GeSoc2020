package Dominio.Egreso.Core;

public class Item {

    private Float valor;
    private String descripcion;

    public Item(Float importe,String desc){
        this.valor=importe;
        this.descripcion=desc;
    }

    public Float getValor() {
        return valor;
    }
    public String getDescripcion() {
        return descripcion;
    }
}
