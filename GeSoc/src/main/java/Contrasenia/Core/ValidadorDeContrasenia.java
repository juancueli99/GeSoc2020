package Contrasenia.Core;

import Contrasenia.DAO.DAOValidacion;
import Contrasenia.DAO.MemoriaValidacion;
import Contrasenia.Excepciones.ExcepcionContraseniaComun;
import Contrasenia.Excepciones.ExcepcionLongitud;
import Contrasenia.Excepciones.ExcepcionNumero;
import Contrasenia.Excepciones.ExcepcionCaracterEspecial;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class ValidadorDeContrasenia {

    private static DAOValidacion repositorio = new MemoriaValidacion();
    //singleton class
    private static List<IValidacion> validaciones = repositorio.obtenerValidaciones();

    public static boolean validarContrasenia(String contrasenia) throws IOException, ExcepcionNumero, ExcepcionLongitud, ExcepcionCaracterEspecial, ExcepcionContraseniaComun {

        AtomicBoolean esSegura = new AtomicBoolean(true);

        validaciones.forEach(validacion-> {
            try {
                validacion.validar(contrasenia);
            }  catch (IOException | ExcepcionContraseniaComun | ExcepcionLongitud | ExcepcionNumero | ExcepcionCaracterEspecial e) {
                e.printStackTrace();
                esSegura.set(false);
            }
        });//cuando tengamos presentacion esos prints vuelan
        // y se tranforman en hermosas mariposas digo... pantallas

        return esSegura.get();
    }
    public static void agregarValidacion(IValidacion nuevaValidacion) {
        validaciones.add(nuevaValidacion);
    }
    public static void removerValidacion(IValidacion viejaValidacion) {
        validaciones.remove(viejaValidacion);
    }
    public static void removerTodasLasValidaciones(){ validaciones.clear(); }
    public static void AgregarTodasLasValidaciones(List<IValidacion>validacioensNuevas){
        validaciones.addAll(validacioensNuevas);
    }

}
