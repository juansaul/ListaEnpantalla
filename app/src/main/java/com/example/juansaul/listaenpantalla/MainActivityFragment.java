package com.example.juansaul.listaenpantalla;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    ArrayAdapter<String> arrAdp;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vGenerada = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> soda = new ArrayList<String>();
        soda.add("naranja 3litros");
        soda.add("fresa 2litros");
        soda.add("toronja 3litros");
        soda.add("piña 1litros");
        soda.add("mango 2litros");
        soda.add("mandarina 4 litros");
        soda.add("tamarindo 2litros");


        arrAdp = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                soda


        );

        ListView vista = (ListView) vGenerada.findViewById(R.id.listView_forecast);
        //(ListView)vGenerada.findViewById(R.id.list_item_forecast_textview);
        vista.setAdapter(arrAdp);

CargadorUsuarios usu = new CargadorUsuarios();
        usu.execute();
        return vGenerada;//inflater.inflate(R.layout.fragment_main, container, false);


        // return inflater.inflate(R.layout.fragment_main, container, false);
    }
//
    private class CargadorUsuarios extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
// es el arreglo de string que saque de consultar libros
            String[] datos = consultarlibros();

            return datos;
        }

        @Override
        protected void onPostExecute(String[] datos) {
            super.onPostExecute(datos);

// los datos los agarra y los mete en la listra de string
            //los limpia la lista anterior y los agrega a la lista
            List<String> listaDatos = Arrays.asList(datos);
            arrAdp.clear();
            arrAdp.addAll(listaDatos);


        }


        public String[] consultarlibros() {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] arregloUsuarios = new String[0];
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://bibliotecawebjuan.ticcode.net/usuario/Ajaxindex";
                // String apiKey = "&APPID=" + "dfb9632bd86e64831b1bc3814bde6a75";
                URL url = new URL(baseUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                // Stream was empty.  No point in parsing.
                if (buffer.length() == 0) {
                    return null;
                }

                //EL JSON en forma de un String muy largo
                // se agarra el json de la api
                forecastJsonStr = buffer.toString();

                //Necesito PARSEAR el JSON para obtener un arreglo de Strings
                //donde se vea la informacion de cada dia

                // aqui se construlle la clase que se usaa abajo
                ExtractorDeDatosJson v = new ExtractorDeDatosJson();

                try {
                    // aqui llego el USUARIOSSTR de estractor de datos
                    // almismo tiempo se invoca y se asigna a el arreglo de usuarios

                    //aqio se invoca la clase y todo el metodo
                    // de aqui se va a l asinktask
                    arregloUsuarios = v.getUsuarios(forecastJsonStr);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("CargadorDePronostico", "El JSON Recibido fue: " + forecastJsonStr);


            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return arregloUsuarios;
        }
    }


   static class ExtractorDeDatosJson {


        public  String[] getUsuarios(String strJSON)
                throws JSONException {
//se crea el arreglo de json que se obtiene de la aplicacion web
            JSONArray Arrayinformacion = new JSONArray(strJSON);
//se crea el arreglo de strings// osea el arreglo de json(toda la linea de jsnon concatenado) se comvierte en string
            String[] usuariosStr = new String[Arrayinformacion.length()];

// aqui se recorre el json buscando los datos que he seleccionado de el json
            for (int i = 0; i < Arrayinformacion.length(); i++) {

                //agarra json individual
                JSONObject UnRegistroJson = Arrayinformacion.getJSONObject(i);
                String nombre = UnRegistroJson.getString("nombre");
                String apellido = UnRegistroJson.getString("apellido");
                String correo = UnRegistroJson.getString("correo");
// aqui introdusco los datos que escoji en el json concatenandolos para que me aparescan en lista
                usuariosStr[i] = nombre + " - " + apellido + " - " + correo;
            }

// aqui envio la informacion que meti en el arreglo de string Usuarios str. y este llega a ....
            return usuariosStr;
        }
    }
}
