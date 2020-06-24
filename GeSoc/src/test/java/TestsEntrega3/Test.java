package TestsEntrega3;

import BandejaMensajes.Mensaje;
import Contrasenia.Excepciones.ExcepcionCaracterEspecial;
import Contrasenia.Excepciones.ExcepcionContraseniaComun;
import Contrasenia.Excepciones.ExcepcionLongitud;
import Contrasenia.Excepciones.ExcepcionNumero;
import Egreso.Core.*;

import Egreso.Core.CriteriosDeCategorizacion.Categoria;
import Egreso.Core.CriteriosDeCategorizacion.Criterio;
import Egreso.Core.CriteriosDeCategorizacion.Jerarquia;
import Rol.Acciones.AgregarJerarquia;
import Rol.Exepciones.NoTengoPermisosException;
import Rol.Rol;
import Rol.RolAdministrador;
import TestsEntrega3.CriterioDummy.CriterioFalla;
import Egreso.Core.CriteriosProveedor.CriterioMenorPrecio;
import Egreso.Validador.Excepciones.NoCumpleValidacionDeCriterioException;
import Egreso.Validador.Excepciones.NoCumpleValidacionException;
import Egreso.Validador.ValidadorDeOperacion;
import Usuario.Usuario;
import org.junit.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    @org.junit.Test
    public void testValidadorNoPasaCriterio(){
        List<Presupuesto> presupuestos=new ArrayList<>();
        presupuestos.add(new Presupuesto(new ArrayList<>(),51000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),52000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),53000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),504000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),55000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),56000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        List<Proveedor> proveedores=new ArrayList();
        proveedores.add(new Proveedor("agdasd","aaaa b4bb","28493672816","bbbb",presupuestos.get(5)));
        proveedores.add(new Proveedor("aydasd","aaaa bbtb","28473672816","cccc",presupuestos.get(1)));
        proveedores.add(new Proveedor("ahdasd","aaaa bcbb","28453672816","dddd",presupuestos.get(2)));
        proveedores.add(new Proveedor("asdasd","aaaa bbbb","28483672816","aaaa",presupuestos.get(0)));
        proveedores.add(new Proveedor("ajdasd","aaaa bhbb","28443672816","eeee",presupuestos.get(3)));
        proveedores.add(new Proveedor("aldasd","aaaa bjbb","28433672816","ffff",presupuestos.get(4)));


        Egreso unEgreso=new Egreso(new Date(),100000, new ArrayList<>(),new MetodoDePago(TipoDeMedioDePago.CHEQUE,"as"),proveedores,new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hubo documento"),new CriterioFalla());
        Mensaje fallo=ValidadorDeOperacion.validarCustomSinBasicas(unEgreso,ValidadorDeOperacion.getValidaciones());
        Assert.assertEquals( new NoCumpleValidacionDeCriterioException().toString(),fallo.getMensajeResultado());
    }
    @org.junit.Test
    public void testValidadorNoPasaOperacion(){
        List<Presupuesto> presupuestos=new ArrayList<>();
        presupuestos.add(new Presupuesto(new ArrayList<>(),51000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),52000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),53000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        List<Proveedor> proveedores=new ArrayList();
        proveedores.add(new Proveedor("asdasd","aaaa bbbb","28483672816","aaaa",presupuestos.get(0)));
        proveedores.add(new Proveedor("agdasd","aaaa b4bb","28493672816","bbbb",presupuestos.get(2)));
        proveedores.add(new Proveedor("aydasd","aaaa bbtb","28473672816","cccc",presupuestos.get(1)));


        Egreso unEgreso=new Egreso(new Date(),51000, new ArrayList<>(),new MetodoDePago(TipoDeMedioDePago.CHEQUE,"as"),proveedores,new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hubo documento"),new CriterioMenorPrecio());
        Mensaje fallo=ValidadorDeOperacion.validarCustomSinBasicas(unEgreso,ValidadorDeOperacion.getValidaciones());
        Assert.assertEquals(new NoCumpleValidacionException().toString(),fallo.getMensajeResultado());
        //no entiendo porque no da este tests
    }

    @org.junit.Test
    public void testJerarquias() throws IOException, ExcepcionNumero, ExcepcionLongitud, ExcepcionCaracterEspecial, ExcepcionContraseniaComun, NoTengoPermisosException, NoCumpleValidacionException, NoCumpleValidacionDeCriterioException {
        RolAdministrador roladmin=new RolAdministrador();
        List<Categoria> categorias=new ArrayList<>();
        categorias.add(new Categoria("descripcion1","nombre1"));
        categorias.add(new Categoria("descripcion2","nombre2"));
        categorias.add(new Categoria("descripcion3","nombre3"));
        categorias.add(new Categoria("descripcion4","nombre4"));
        Criterio criterioDummy=new Criterio(categorias,"dummy","dummy");
        Jerarquia hoja=new Jerarquia(criterioDummy,new ArrayList<>());
        List<Jerarquia>hojas=new ArrayList<>();
        hojas.add(hoja);
        Jerarquia jerarquiaNueva= new Jerarquia(criterioDummy,hojas);
        AgregarJerarquia agregarJerarquia=new AgregarJerarquia(criterioDummy,jerarquiaNueva);
        roladmin.getAcciones().add(agregarJerarquia);
        List<Rol>roles=new ArrayList<>();
        roles.add(roladmin);
        Usuario unUsuario=new Usuario(roles,"pepito","SiestaContr4senia no funca me m@deo");
        List<Presupuesto> presupuestos=new ArrayList<>();
        presupuestos.add(new Presupuesto(new ArrayList<>(),51000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),52000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),53000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),54000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),55000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));

        List<Proveedor> proveedores=new ArrayList<>();
        proveedores.add(new Proveedor("asdasd","aaaa bbbb","28483672816","aaaa",presupuestos.get(0)));
        proveedores.add(new Proveedor("agdasd","aaaa b4bb","28493672816","bbbb",presupuestos.get(2)));
        proveedores.add(new Proveedor("aydasd","aaaa bbtb","28473672816","cccc",presupuestos.get(1)));
        proveedores.add(new Proveedor("aydasd","aa6a bbtb","28573672816","ccfc",presupuestos.get(3)));
        proveedores.add(new Proveedor("aydasd","aata bbtb","28773672816","ccgc",presupuestos.get(4)));

        Egreso unEgreso=new Egreso(new Date(),53000, new ArrayList<>(),new MetodoDePago(TipoDeMedioDePago.CHEQUE,"as"),proveedores,new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hubo documento"),new CriterioMenorPrecio());
        roladmin.realizarAccion(agregarJerarquia);
        int cantidad_criterios=agregarJerarquia.getJerarquiaAsociada().getHijos().size()+1+agregarJerarquia.getJerarquiaAsociada().getHijos().get(0).getHijos().size();
        Assert.assertEquals(cantidad_criterios,3);
    }

    @org.junit.Test
    public void testValidadorPasaOperacion(){
        List<Presupuesto> presupuestos=new ArrayList<>();
        presupuestos.add(new Presupuesto(new ArrayList<>(),51000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),52000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),53000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),54000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));
        presupuestos.add(new Presupuesto(new ArrayList<>(),55000,new ArrayList<>(),new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hay doc")));

        List<Proveedor> proveedores=new ArrayList();
        proveedores.add(new Proveedor("asdasd","aaaa bbbb","28483672816","aaaa",presupuestos.get(0)));
        proveedores.add(new Proveedor("agdasd","aaaa b4bb","28493672816","bbbb",presupuestos.get(2)));
        proveedores.add(new Proveedor("aydasd","aaaa bbtb","28473672816","cccc",presupuestos.get(1)));
        proveedores.add(new Proveedor("aydasd","aa6a bbtb","28573672816","ccfc",presupuestos.get(3)));
        proveedores.add(new Proveedor("aydasd","aata bbtb","28773672816","ccgc",presupuestos.get(4)));



        Egreso unEgreso=new Egreso(new Date(),53000, new ArrayList<>(),new MetodoDePago(TipoDeMedioDePago.CHEQUE,"as"),proveedores,new DocumentoComercial(TipoDocumentoComercial.SIN_DOCUMENTO,"no hubo documento"),new CriterioMenorPrecio());
        Mensaje cumplio=ValidadorDeOperacion.validarCustomSinBasicas(unEgreso,ValidadorDeOperacion.getValidaciones());
        Assert.assertEquals("Paso exitosamente todas las Validaciones",cumplio.getMensajeResultado());
        //no entiendo porque no da este tests
    }
}
