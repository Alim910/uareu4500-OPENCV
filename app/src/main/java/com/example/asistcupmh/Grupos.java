package com.example.asistcupmh;

import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Grupos extends AppCompatActivity {
    private Spinner spiny;
    private ListView listk;
    String showUrl = "http://alixmpledcwam.000webhostapp.com/practica1/mostraralumnclass.php";
    RequestQueue requestQueue,requestQueue2;
    String[] ListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
        spiny=(Spinner) findViewById(R.id.pinner);

        String[] datos= new String[]{"Elige una opcion","Patos","Puerquitos"};
        ArrayAdapter<String> adaptador= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,datos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiny.setAdapter(adaptador);
        listk=(ListView) findViewById(R.id.liston);
       // requestQueue= Volley.newRequestQueue(this);
        requestQueue= Volley.newRequestQueue(getApplicationContext());








        spiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:

                        StringRequest request = new StringRequest(Request.Method.POST, showUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONObject jsonObject= new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("listaClase");
                                    ListData = new String[jsonArray.length()];

                                        for (int i=0; i<jsonArray.length(); i++) {

                                            JSONObject object = jsonArray.getJSONObject(i);
                                            String idUsuario = object.getString("idUsuario");
                                            String nombreCompleto = object.getString("nombreCompleto");
                                            ListData[i] = object.getString("idUsuario");
                                            ListData[i] = object.getString("nombreCompleto");
                                            System.out.println(nombreCompleto+idUsuario);

                                           if(ListData.length == 0){
                                                System.out.println("Esta vacio");
                                            }else{
                                                System.out.println(Arrays.toString(ListData));
                                                checkList(); }
                                        }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Grupos.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Grupos.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<String, String>();;
                                parametros.put("theid", "237");
                                return parametros;

                            }

                        };
                        //requestQueue.getCache().clear();
                        requestQueue.add(request);


                        break;
                    case 2:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    SparseBooleanArray sparseBooleanArray ;

    public void checkList() {
        //String[] ListData= new String[]{"Hampshire","Pietrain","MiniPig"};
        //ArrayAdapter<String> adaptador2= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,ListData);
        ArrayAdapter<String> adaptador2= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,android.R.id.text1,ListData);

        adaptador2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        listk.setAdapter(adaptador2);

        listk.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sparseBooleanArray = listk.getCheckedItemPositions();

                String ValueHolder = "" ;

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {

                        ValueHolder += ListData [ sparseBooleanArray.keyAt(i) ] + ",";
                    }

                    i++ ;
                }

                ValueHolder = ValueHolder.replaceAll("(,)*$", "");

                Toast.makeText(Grupos.this, "ListView Selected Values = " + ValueHolder, Toast.LENGTH_LONG).show();

            }
        });

    }
}
