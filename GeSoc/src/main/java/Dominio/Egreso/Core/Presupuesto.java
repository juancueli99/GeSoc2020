package Dominio.Egreso.Core;

import Dominio.Egreso.Core.CriteriosDeCategorizacion.Criterio;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dom_presupuestos")
public class Presupuesto {
    @Id
    @GeneratedValue
    private int presupuesto;

    @Transient
    private List<Criterio> criterios;

    @Column(name = "valor")
    private float valor;

    @Column(name = "fecha_creado", columnDefinition = "DATE")
    private LocalDate fecha;

    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL)
    private List<Detalle> detalles;

    @OneToOne
    @JoinColumn(name = "documento_comercial")
    private DocumentoComercial documentoComercial;

    @ManyToOne
    @JoinColumn(name = "proveedor", referencedColumnName = "proveedor")
    private Proveedor proveedor;

    public Presupuesto(List<Criterio> criterios, float valor, List<Detalle> detalles, DocumentoComercial documentoComercial) {
        this.criterios = criterios;
        this.valor = valor;
        this.detalles = detalles;
        this.documentoComercial = documentoComercial;
        this.fecha = LocalDate.now();
    }

    public List<Criterio> getCriterios() {
        return criterios;
    }
    public float getValor() { return valor;}
    public List<Detalle> getDetalles() {return detalles;}
    public DocumentoComercial getDocumentoComercial() { return documentoComercial;}
    public LocalDate getFecha() {return fecha; }
}
