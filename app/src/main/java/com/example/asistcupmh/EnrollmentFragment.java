package com.example.asistcupmh;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class EnrollmentFragment extends Fragment {
    private Spinner spinyE;
    private ListView listE;
    String showClass = "https://alixmpledcwam.000webhostapp.com/practica1/mostrarclasss.php";
    String showUrl = "https://alixmpledcwam.000webhostapp.com/practica1/mostraralumnclass.php";
    RequestQueue requestQueueE,requestQueueE2;
    //String[] ListDataE;
    List<StringWithTag> listAlumn = new ArrayList<StringWithTag>();
    List<StringWithTag> list = new ArrayList<StringWithTag>();
    private FragmentAdapter adapter;
    ViewPager mViewPager;
    Session session;
    String idProfesrSess;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_enrollment,container,false);
        spinyE=(Spinner) rootView.findViewById(R.id.pinnerE);
        listE=(ListView)rootView.findViewById(R.id.listonE);
        requestQueueE= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueueE2= Volley.newRequestQueue(getActivity().getApplicationContext());
        session = new Session(getActivity().getApplicationContext());
        idProfesrSess = session.getid();



        if(!list.equals(null)){
            list.clear();

        }

        consultClass();

        spinyE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                if (listAlumn.size() != 0) {
                                    System.out.println("Esta LLENA la lista de alumnos");
                                    listAlumn.clear();
                                } else {

                                }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        if(object.getString("idUsuario") == "0") {
                                            System.out.println("no hay alumnos");
                                        }else{

                                        String idUsuario = object.getString("idUsuario");
                                        String nombreCompleto = object.getString("nombreCompleto");
                                        listAlumn.add(new StringWithTag(object.getString("nombreCompleto"), object.getString("idUsuario")));
                                        System.out.println(nombreCompleto + idUsuario);
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
                            Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
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
                    requestQueueE2.add(request);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        return rootView;
    }


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
                        System.out.println(materia);

                    }
                    if(list.size() == 0){
                        System.out.println("Esta vacio tus grupos");
                    }else{
                        System.out.println(list.toString());

                        ArrayAdapter<StringWithTag> adap = new ArrayAdapter<StringWithTag> (getActivity(), android.R.layout.simple_spinner_item, list);
                        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinyE.setAdapter(adap);
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
        requestQueueE.add(request);
    }


    public void checkList() {
        ArrayAdapter<StringWithTag> adaptador2= new ArrayAdapter<StringWithTag>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_activated_1,android.R.id.text1,listAlumn);
        //ArrayAdapter<String> adaptador2= new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_activated_1,android.R.id.text1,ListDataE);

        adaptador2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        listE.setAdapter(adaptador2);

        listE.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String getAlumno= parent.getItemAtPosition(position).toString();

                StringWithTag getStri = (StringWithTag) parent.getItemAtPosition(position);
                Object getTagi = getStri.tag;

                UserInfo nextFrag= new UserInfo();
                Bundle args = new Bundle();
                args.putString(nextFrag.DATA_RECEIVE, getAlumno);
                args.putString(nextFrag.DATA_ID_RECEIVE, getTagi.toString());
                nextFrag.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();



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

