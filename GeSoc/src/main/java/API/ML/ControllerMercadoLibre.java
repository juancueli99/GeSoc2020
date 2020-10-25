package API.ML;

import API.ML.DTOs.*;
import API.ML.Excepciones.ExcepcionCiudadNoEncontrada;
import API.ML.Excepciones.ExcepcionNoSePudoConvertir;
import API.ML.Excepciones.ExcepcionProvinciaNoEncontrada;
import API.ML.Excepciones.NoExisteMonedaException;
import API.RequestMaker.RequestMaker;
import Lugares.Ciudad;
import Lugares.Pais;
import Lugares.Provincia;
import Persistencia.DAO.DAOMemoria;
import Persistencia.Repositorio;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ControllerMercadoLibre {

    private String urlDominio="https://api.mercadolibre.com";

    private static ControllerMercadoLibre instancia=null;


    public Repositorio paisesRepo = new Repositorio(new DAOMemoria<Pais>());
    public Repositorio provinciasRepo = new Repositorio(new DAOMemoria<Provincia>());
    public Repositorio ciudadesRepo = new Repositorio(new DAOMemoria<Ciudad>());
    public Repositorio monedasRepo = new Repositorio(new DAOMemoria<MonedaDTO>());

    private ControllerMercadoLibre() throws IOException {
        pedirPaises();
        paisesRepo.getTodosLosElementos().forEach(pais->{Pais unPais = (Pais)pais;
            try {
                unPais.getProvincias().addAll(this.obtenerLasProviciasDeUnPais(unPais.getId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        List<List<Provincia>> listaDeProvincias = paisesRepo.getTodosLosElementos().stream().map(pais->{Pais unPais = (Pais)pais;
        return unPais.getProvincias();}).collect(Collectors.toList());

        List<Provincia> provincias = listaDeProvincias.stream().flatMap(List::stream).collect(Collectors.toList());

        provincias.forEach(provincia-> {
            try {
                provincia.getCiudades().addAll(this.obtenerLasCiudadesDeUnaProvincia(provincia.getId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pedirMonedas();
    }

    public ConversionDTO convertirMoneda(String nombreMonedaActual, String nombreMonedaAConvertir) throws IOException, ExcepcionNoSePudoConvertir, NoExisteMonedaException {

        MonedaDTO monedaActual=getMoneda(nombreMonedaActual);
        MonedaDTO monedaAConvertir=getMoneda(nombreMonedaAConvertir);
        if(monedaActual==null){
            throw new NoExisteMonedaException(nombreMonedaActual);
        }
        if(nombreMonedaAConvertir==null){

            throw new NoExisteMonedaException(nombreMonedaAConvertir);
        }
        String request="/currency_conversions/search?from="+monedaActual.getId()+"&to="+monedaAConvertir.getId();
        HttpEntity entidad= crearRequest(request);
        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");
        ConversionDTO conversion=null;
        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();
            conversion=crearDTOConversion(responseObj,monedaActual,monedaAConvertir);
        }else{

            throw new ExcepcionNoSePudoConvertir(nombreMonedaActual,nombreMonedaAConvertir);
        }

        return conversion;


    }






    private void pedirMonedas() throws IOException {
        HttpEntity entidad= crearRequest("/currencies");
        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");
        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonArray responseObj = parser.parse(responseStr).getAsJsonArray();
            responseObj.forEach(jsonElemnt-> monedasRepo.agregar(new MonedaDTO(
                    jsonElemnt.getAsJsonObject().get("symbol").getAsString(),
                    jsonElemnt.getAsJsonObject().get("decimal_places").getAsInt(),
                    jsonElemnt.getAsJsonObject().get("description").getAsString(),
                    jsonElemnt.getAsJsonObject().get("id").getAsString())));
        }

    }

    private HttpEntity crearRequest(String pedido) throws IOException {
        String url = urlDominio+pedido;
      return RequestMaker.getInstance().crearGET(url);
    }

    private void pedirPaises() throws IOException {
        HttpEntity entidad = crearRequest("/classified_locations/countries");

        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");

        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonArray responseObj = parser.parse(responseStr).getAsJsonArray();
            responseObj.forEach(jsonElemnt -> {
                paisesRepo.agregar(new Pais(
                        jsonElemnt.getAsJsonObject().get("id").getAsString(),
                        jsonElemnt.getAsJsonObject().get("name").getAsString(),
                        jsonElemnt.getAsJsonObject().get("locale").getAsString(),
                        jsonElemnt.getAsJsonObject().get("currency_id").getAsString()));
            });
            
        }
    }

    public List<ProvinciaDTO> obtenerProvinciasDe(String id_pais) throws IOException {
        List<ProvinciaDTO>lista=new ArrayList<>();

        HttpEntity entidad = crearRequest("/classified_locations/countries/"+id_pais);

        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");
        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();
            responseObj.getAsJsonArray("states").forEach(state-> {
                try {
                    lista.add(obtenerDatosProvincia( state.getAsJsonObject().get("id").getAsString()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ExcepcionProvinciaNoEncontrada excepcionProvinciaNoEncontrada) {
                    excepcionProvinciaNoEncontrada.printStackTrace();
                }
            });
        }
        return lista;
    }
    public static ControllerMercadoLibre getControllerMercadoLibre(){
        if(instancia==null){
            try {
                instancia=new ControllerMercadoLibre();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instancia;
    }

    public Pais getPais(String nombrePais) {
        return (Pais) paisesRepo.buscarPorNombre(nombrePais);
    }

    public MonedaDTO getMoneda(String nombreMoneda) {
        return (MonedaDTO) monedasRepo.buscarPorNombre(nombreMoneda);
    }

    public MonedaDTO getMonedaByID(String idMoneda) {
        return (MonedaDTO) monedasRepo.buscarPorId(idMoneda);
    }

    private ZipCodeDTO crearDTOZipCode(JsonObject responseObj) {

        int zipCode=responseObj.get("zip_code").getAsInt();
        JsonObject stateJson= responseObj.getAsJsonObject("state");
        Provincia state=new Provincia(stateJson.get("id").getAsString(),stateJson.get("name").getAsString());
        JsonObject countryJson= responseObj.getAsJsonObject("country");
        Pais country=new Pais(countryJson.get("id").getAsString(),countryJson.get("name").getAsString());
        JsonObject cityJson= responseObj.getAsJsonObject("city");
        Ciudad city=new Ciudad(cityJson.get("id").getAsString(),cityJson.get("name").getAsString());

        return new ZipCodeDTO(state,country,zipCode,city);
    }

    private ConversionDTO crearDTOConversion(JsonObject responseObj, MonedaDTO monedaActual, MonedaDTO monedaAConvertir) {

        JsonObject ratio= responseObj.getAsJsonObject("ratio");
        JsonObject ratioMercadoPago= responseObj.getAsJsonObject("mercado_pago_ratio");
        return new ConversionDTO(monedaActual,monedaAConvertir,ratio.getAsDouble(),ratioMercadoPago.getAsDouble());
    }

    //PROVINCIAS

    private ProvinciaDTO crearDTOProvincia(JsonObject responseObj) {
        JsonObject id= responseObj.getAsJsonObject("id");
        JsonObject nombre= responseObj.getAsJsonObject("name");
        JsonArray ciudades= responseObj.getAsJsonArray("cities");
        ProvinciaDTO nuevaProvincia= new ProvinciaDTO(id.getAsString(),nombre.getAsString());
        return nuevaProvincia;
    }

    private ProvinciaDTO obtenerDatosProvincia(String id) throws IOException, ExcepcionProvinciaNoEncontrada {

        String request="/classified_locations/states/"+id;
        HttpEntity entidad = crearRequest(request);
        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");
        ProvinciaDTO nuevaProvincia = null;
        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();
            nuevaProvincia=crearDTOProvincia(responseObj);
        }else{

            throw new ExcepcionProvinciaNoEncontrada(id);
        }

        return nuevaProvincia;
    }

    public Provincia generarDatosDeProvincia(String idProvincia) throws IOException, ExcepcionProvinciaNoEncontrada {

        ProvinciaDTO busqueda = (ProvinciaDTO) provinciasRepo.buscarPorId(idProvincia);
        Provincia retorno=null;
        if(busqueda==null) {
            busqueda= obtenerDatosProvincia(idProvincia);
            provinciasRepo.agregar(busqueda);
        }
        retorno=new Provincia(busqueda.getId(),busqueda.getName());
        return retorno;
    }

    public List<Provincia> obtenerLasProviciasDeUnPais(String idPais) throws IOException {

        List<Provincia> retorno = new ArrayList<Provincia>();

        HttpEntity entidad = crearRequest("/classified_locations/countries/" + idPais);

        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");

        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();

            responseObj.getAsJsonArray("states").forEach
                    (x->retorno.add(new Provincia(x.getAsJsonObject().get("id").getAsString(),
                            x.getAsJsonObject().get("name").getAsString())));
        }

        return retorno;
    }

    //CIUDADES

    private CiudadDTO crearDTOCiudad(JsonObject responseObj) {
        JsonObject id= responseObj.getAsJsonObject("id");
        JsonObject nombre= responseObj.getAsJsonObject("name");
        JsonArray ciudades= responseObj.getAsJsonArray("cities");
        CiudadDTO nuevaCiudad= new CiudadDTO(id.getAsString(),nombre.getAsString());
        return nuevaCiudad;
    }

    private CiudadDTO obtenerDatosCiudad(String id) throws IOException, ExcepcionCiudadNoEncontrada {

        String request="/classified_locations/cities/"+id;
        HttpEntity entidad = crearRequest(request);
        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");
        CiudadDTO nuevaCiudad = null;
        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();
            nuevaCiudad=crearDTOCiudad(responseObj);
        }else{

            throw new ExcepcionCiudadNoEncontrada(id);
        }

        return nuevaCiudad;
    }

    public Ciudad generarDatosDeCiudad(String idCiudad) throws IOException, ExcepcionCiudadNoEncontrada {

        CiudadDTO busqueda = (CiudadDTO) ciudadesRepo.buscarPorId(idCiudad);
        Ciudad retorno=null;
        if(busqueda==null) {
            busqueda= obtenerDatosCiudad(idCiudad);
            ciudadesRepo.agregar(busqueda);
        }
        retorno=new Ciudad(busqueda.getId(),busqueda.getName());
        return retorno;
    }

    public List<Ciudad> obtenerLasCiudadesDeUnaProvincia(String idProvincia) throws IOException {

        List<Ciudad> retorno = new ArrayList<Ciudad>();

        HttpEntity entidad = crearRequest("/classified_locations/states/" + idProvincia);

        String responseStr = IOUtils.toString(entidad.getContent(), "UTF-8");

        if (responseStr != null && !responseStr.isEmpty()) {
            JsonParser parser = new JsonParser();
            JsonObject responseObj = parser.parse(responseStr).getAsJsonObject();

            responseObj.getAsJsonArray("cities").forEach
                    (x->retorno.add(new Ciudad(x.getAsJsonObject().get("id").getAsString(),
                            x.getAsJsonObject().get("name").getAsString())));
        }

        return retorno;
    }

}
