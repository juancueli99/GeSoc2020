package Operacion.Validador;

import Operacion.Core.Operacion;
import Operacion.Validador.Excepciones.NoCumpleValidacionException;
import Usuario.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ValidadorDeOperacion {
    static List<ValidacionOperacion> validaciones = new ArrayList() {{

    }};
    static EstrategiaRevision estrategia;
    static List<Mensaje> mensajes = new ArrayList();

    public static void validarCustomSinBasicas(Operacion unaOperacion, List<ValidacionOperacion> validacionesEspecificas, Usuario unUsuario) {
        validacionesEspecificas.forEach(validacion -> {
            try {
                validacion.validar(unaOperacion);
            } catch (NoCumpleValidacionException e) {
                agregarValidacionFallida(unaOperacion,unUsuario);
            }
        });
        agregarValidacionExitosa(unaOperacion,unUsuario);
    }

    public static void validarCustomConBasicas(Operacion unaOperacion, List<ValidacionOperacion> validacionesEspecificas, Usuario unUsuario) {
        validarDefault(unaOperacion,unUsuario);
        validarCustomSinBasicas(unaOperacion, validacionesEspecificas,unUsuario);
    }

    public static void asignarRevisorA(Operacion unaOperacion, Usuario revisor) {
        estrategia.revisar(unaOperacion,revisor);
    }

    public static void validarDefault(Operacion unaOperacion, Usuario unUsuario) {
        validaciones.forEach(validacion -> {
            try {
                validacion.validar(unaOperacion);
            } catch (NoCumpleValidacionException e) {
                agregarValidacionFallida(unaOperacion,unUsuario);
            }
        });
        agregarValidacionExitosa(unaOperacion,unUsuario);
    }
    public static void agregarValidacionExitosa(Operacion unaOperacion, Usuario unUsuario){
        //Creo el msj que ahora es una clase
    }
    public static void agregarValidacionFallida(Operacion unaOperacion, Usuario unUsuario){
        //Creo el msj que ahora es una clase
    }
}