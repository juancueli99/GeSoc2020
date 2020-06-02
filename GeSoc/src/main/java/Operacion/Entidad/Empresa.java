package Operacion.Entidad;

import Operacion.Core.Operacion;

import java.util.List;

public class Empresa extends EntidadJuridica {

    Categoria categoria;

    public Empresa(String nombreEntidad, String descripcionEntidad, List<Operacion> operacionesEntidad,
                   String rs,String cuitEntidad,String cp,String ci,String tipo,
                   TipoActividad actividad,Integer personal,Float promedio){
        super(nombreEntidad,descripcionEntidad,operacionesEntidad,
                rs,cuitEntidad,cp,ci,tipo,actividad,personal,promedio);

        this.determinarCategoria();
    }

    public void determinarCategoria(){
        Actividad miActividad=new Actividad(this.actividad);
        this.categoria=miActividad.calcularCategoria(this);

    }
}