package com.example.asistcupmh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class GruposFragment extends Fragment {
    private FloatingActionButton fab;
    private Spinner spiny;
    private ListView listk;
    String showClass = "http://alixmpledcwam.000webhostapp.com/practica1/mostrarclasss.php";
    String showUrl = "http://alixmpledcwam.000webhostapp.com/practica1/mostraralumnclass.php";
    RequestQueue requestQueue,requestQueue2;
    String[] ListData;
    List<StringWithTag> list = new ArrayList<StringWithTag>();
    Session session;
    String idProfesrSess;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_groups,container,false);
        spiny=(Spinner) rootView.findViewById(R.id.pinner);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        session = new Session(getActivity().getApplicationContext());
        idProfesrSess = session.getid();
        listk=(ListView)rootView.findViewById(R.id.liston);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue2= Volley.newRequestQueue(getActivity().getApplicationContext());


        consultClass();

        spiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag s = (StringWithTag) parent.getItemAtPosition(position);
                final Object tag = s.tag;
                String label = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "You selected: " + tag,Toast.LENGTH_LONG).show();

                if(tag.equals(null)){
                    System.out.println("no hay id seleccionado");
                }else{
                    StringRequest request = new StringRequest(Request.Method.POST, showUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("listaClase");
                            ListData = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);
                                String idUsuario = object.getString("idUsuario");
                                String nombreCompleto = object.getString("nombreCompleto");
                                ListData[i] = object.getString("idUsuario");
                                ListData[i] = object.getString("nombreCompleto");
                                System.out.println(nombreCompleto + idUsuario);

                                if (ListData.length == 0) {
                                    System.out.println("Esta vacia la lista de alumnos");
                                } else {
                                    //System.out.println(Arrays.toString(ListData));
                                    checkList();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        ;
                        parametros.put("theid", tag.toString());
                        return parametros;

                    }

                };
                requestQueue2.add(request);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        return rootView;
    }

    SparseBooleanArray sparseBooleanArray ;

    public void consultClass() {
        StringRequest request = new StringRequest(Request.Method.POST, showClass, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("listaClasesProf");

                    for (int i=0; i<jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);
                        String id = object.getString("IDGRUPO");
                        String materia = object.getString("GRUPO");
                        list.add(new StringWithTag(object.getString("GRUPO"), object.getString("IDGRUPO")));
                        //ListData2[i] = object.getString("GRUPO");
                        System.out.println(materia);

                    }
                    if(list.size() == 0){
                        System.out.println("Esta vacio tus grupos");
                    }else{
                        System.out.println(list.toString());

                        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getActivity(), android.R.layout.simple_spinner_item, list);
                        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spiny.setAdapter(adap);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();;
                parametros.put("idProf", idProfesrSess);
                return parametros;

            }

        };
        //requestQueue.getCache().clear();
        requestQueue.add(request);
    }

    public void checkList() {
        ArrayAdapter<String> adaptador2= new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1,android.R.id.text1,ListData);

        adaptador2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        listk.setAdapter(adaptador2);

        listk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

    }

    public class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
