package Dominio.Ingreso;

import Dominio.Egreso.Core.Egreso;
import Dominio.Entidad.Entidad;
import Dominio.Ingreso.Excepciones.NoPuedoAsignarMasDineroDelQueTengoException;
import Dominio.Moneda.Valor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dom_ingresos")
public class Ingreso {
    @Id
    @GeneratedValue
    private int ingreso;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "valor")
    private Valor valor;

    @Column(name = "fecha", columnDefinition = "DATE")
    private LocalDate fecha;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "entidad", referencedColumnName = "entidad")
    private Entidad entidad;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL)
    private List<Egreso> gastadoEn;

   public Ingreso(String pais, double importe, LocalDate fecha,String descripcion,List<Egreso>egresos){
       this.valor= new Valor(pais,importe);
       this.fecha=fecha;
       this.descripcion=descripcion;
       this.gastadoEn=egresos;
   }

   //GETTERS
    public Valor getValor() {
        return valor;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public List<Egreso> getGastadoEn() {
        return gastadoEn;
    }
    public void agregarEgreso(Egreso unEgreso) throws NoPuedoAsignarMasDineroDelQueTengoException {
       double gastoTotal=gastadoEn.stream().mapToDouble(egreso->egreso.getValor().getImporte()).sum()+unEgreso.getValor().getImporte();
       if(gastoTotal<this.valor.getImporte()){
           gastadoEn.add(unEgreso);
       }else{
           throw new NoPuedoAsignarMasDineroDelQueTengoException();
       }

    }

    public int getIngreso() {
        return ingreso;
    }
}
