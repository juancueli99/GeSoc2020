package Servidor.Controllers;

import Dominio.Entidad.Entidad;
import Dominio.Ingreso.Ingreso;
import Dominio.Usuario.Usuario;
import Persistencia.DAO.DAO;
import Persistencia.DAO.DAOBBDD;
import Persistencia.Repos.Repositorio;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ControllerIngresos {
    public static ModelAndView visualizarPantalla(Request request, Response response){

        Map<String,Object> datos = new HashMap<>();

        Usuario miUsuario= ControllerSesion.obtenerUsuariodeSesion(request);

        List<Entidad> entidades= miUsuario.getEgresosAREvisar().stream().map(e->e.getEntidad()).collect(Collectors.toList());

        Set<Entidad> setEntidades= new HashSet<Entidad>();
        setEntidades.addAll(entidades);
        entidades.clear();
        entidades.addAll(setEntidades);

        datos.put("entidades",entidades);
        datos.put("egreso",miUsuario.getEgresosAREvisar());

        ModelAndView vista = new ModelAndView(datos, "cargar_ingreso.html");

        return vista;
    }

    public static ModelAndView visualizarPantallaDetalleIngreso(Request request, Response response) {

        Map<String, Object> datos= new HashMap<>();

        String ingreso = request.queryParams("ingreso");
        Usuario miUsuario= ControllerSesion.obtenerUsuariodeSesion(request);
        List<Entidad> entidades= miUsuario.getEgresosAREvisar().stream().map(e->e.getEntidad()).collect(Collectors.toList());
        Set<Entidad> entidadSet= new HashSet<>();
        entidadSet.addAll(entidades);
        entidades.clear();
        entidadSet.addAll(entidadSet);

        datos.put("entidades", entidades);

        List<Ingreso> ingresosQueManejo= entidades.stream().map(e->e.getIngresos()).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());

        datos.put("ingresos",ingresosQueManejo);
        List<Ingreso> posiblesIngresos= ingresosQueManejo.stream().filter(in->in.getIngreso()==Integer.valueOf(ingreso)).collect(Collectors.toList());

        if(!posiblesIngresos.isEmpty()){
            datos.put("ingreso", posiblesIngresos.get(0));
        }

        datos.put("categorias", entidades.stream().map(e->e.getCriterios()).flatMap(List::stream).collect(Collectors.toList()).stream().map(cri-> cri.getCategoriaCriterios()).flatMap(List::stream).collect(Collectors.toList()));
        //datos.put("egreso",egresosARevisar);
        return new ModelAndView(datos, "detalle_ingreso.html");
    }

    public static Object cargarIngreso(Request request, Response response) {

        String entidad = request.queryParams("entidad"); //No lo tengo en mi constructor -> es necesario?
        String fecha = request.queryParams("fecha");
        String moneda = request.queryParams("moneda");
        String importe = request.queryParams("importe");
        String descripcion = request.queryParams("descripcion");

        List egresosAsociados = new ArrayList();

        Ingreso ingreso = new Ingreso(moneda, Double.parseDouble(importe), LocalDate.parse(fecha),LocalDate.now(), descripcion, egresosAsociados);

        persistirIngreso(ingreso);

        response.redirect("cargar_ingreso");

        return null;
    }

    public static void persistirIngreso(Ingreso ingreso){

        DAO DAOIngreso = new DAOBBDD<Ingreso>(Ingreso.class); //dao generico de BBDD
        Repositorio repoIngreso = new Repositorio<Ingreso>(DAOIngreso); //repositorio que tambien usa generics

        if(!repoIngreso.existe(ingreso))
            repoIngreso.agregar(ingreso);

    }
}
