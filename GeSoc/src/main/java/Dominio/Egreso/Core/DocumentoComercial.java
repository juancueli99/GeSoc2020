package Dominio.Egreso.Core;

public class DocumentoComercial {

    private TipoDocumentoComercial tipo;
    private String detalle;
    private String rutaDeArchivo;

    //No hago setters porque si le cambio al documento el tipo rompo muchas cosas :(
    public DocumentoComercial(TipoDocumentoComercial documento,String dato){
        this.tipo=documento;
        this.detalle=dato;
    }

    public String getDetalle() {
        return detalle;
    }
    public TipoDocumentoComercial getTipo() {
        return tipo;
    }
    public String getRutaDeArchivo() { return rutaDeArchivo; }
    public void setRutaDeArchivo(String rutaDeArchivo) { this.rutaDeArchivo = rutaDeArchivo;}
}