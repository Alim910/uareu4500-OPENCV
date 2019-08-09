package com.example.asistcupmh;

import android.app.DatePickerDialog;
//import android.icu.util.Calendar;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;


public class AttendanceFragment extends Fragment {
    private static final int CHECK_LIST = 111;
    private Button startAttendance,finishAttendance;
    private ListView listk;
    SparseBooleanArray sparseBooleanArray ;
    List<StringWithTag2> listAlumn = new ArrayList<StringWithTag2>();
   //String[] ListData=new String[0];
    int dia,mes,ano;
    private EditText et;
    Date finaldate;
    private Spinner spiny;
    String idProfesrSess;
    String showClass = "https://alixmpledcwam.000webhostapp.com/practica1/showmyclases.php";
    String showUrl = "https://alixmpledcwam.000webhostapp.com/practica1/mostraralumnbyclass.php";
    String insertPL = "https://alixmpledcwam.000webhostapp.com/practica1/putIdLista.php";
    String insertAlumnPL = "https://alixmpledcwam.000webhostapp.com/practica1/putAlumnosLista.php";
    RequestQueue requestQueue,requestQueue2,requestQueue3,requestQueue4,requestQueue5;
    List<StringWithTag2> list = new ArrayList<StringWithTag2>();
    private Session session;
    String currentCheckyID=null;
    String[] days_dias = {"-","Domingo","Lunes" , "Martes" ,"Miércoles" ,"Jueves" , "Viernes" , "Sábado" };
    String bg;
    String chossenIdClassToDB, chossenDateToDB="",chossenTimeToDB;
    String idClase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_attendance,container,false);
        spiny=(Spinner) rootView.findViewById(R.id.pinner);
        listk=(ListView)rootView.findViewById(R.id.liston);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue2= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue3= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue4= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue5= Volley.newRequestQueue(getActivity().getApplicationContext());
        startAttendance=(Button)rootView.findViewById(R.id.btnStartAttendance);
        finishAttendance=(Button)rootView.findViewById(R.id.btnfinish);
        et = (EditText) rootView.findViewById(R.id.editText5);
        Calendar c = Calendar.getInstance();
        String currentdate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        chossenTimeToDB = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        et.setText("---Elige la fecha---");
        session = new Session(getActivity().getApplicationContext());
        idProfesrSess = session.getid();


        list.add(new StringWithTag2( "-------Elige una clase----",-1));
        //startAttendance.setEnabled(true);

        startAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Attendance.class);
                if(chossenIdClassToDB==null){

                }else{
                    intent.putExtra("IDCLASS",chossenIdClassToDB);
                    startActivityForResult(intent, CHECK_LIST);
                }


            }
        });
        finishAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(chossenIdClassToDB+"--"+chossenDateToDB+"--"+chossenTimeToDB);

                //INSERTAR ID PASE DE LISTA
                StringRequest request4 = new StringRequest(Request.Method.POST, insertPL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                            try {

                                JSONObject jsonObject= new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("listaId");

                                JSONObject object = jsonArray.getJSONObject(0);
                                String resultadoIdPS = object.getString("CLAVE");
                                if(resultadoIdPS != "0"){
                                    System.out.println("LOS DATOS DE LA CLASE FUERON SUBIDOS CON EXITO");
                                    int cntChoice = listk.getCount();

                                    String checked = "";

                                    String unchecked = "";
                                    SparseBooleanArray sparseBooleanArray = listk.getCheckedItemPositions();

                                    for(int i = 0; i < cntChoice; i++)
                                    {
                                        StringWithTag2 getStri1 = (StringWithTag2) listk.getItemAtPosition(i);
                                        final Object getTagi1 = getStri1.tag;

                                        if(sparseBooleanArray.get(i) == true)
                                        {
     /*METER ALUMNOS ASISTIDOS*/            checked += getTagi1.toString() +"." + "\t";
                                            StringRequest request2 = new StringRequest(Request.Method.POST, insertAlumnPL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d(TAG, response.toString());
                                                        try {

                                                            JSONObject jsonObject= new JSONObject(response);
                                                            JSONArray jsonArray = jsonObject.getJSONArray("listaAlumno");

                                                            JSONObject object = jsonArray.getJSONObject(0);
                                                            String resultadoInsrtAl = object.getString("CLAVE");
                                                            if(resultadoInsrtAl != "0"){
                                                                System.out.println("Se inserto correctamente a: "+getTagi1.toString());
                                                            }else{

                                                            }

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
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
                                                    Map<String, String> parametros2 = new HashMap<String, String>();;
                                                    parametros2.put("alumny", getTagi1.toString());
                                                    parametros2.put("statusy", "PRESENTE");
                                                    return parametros2;

                                                }

                                            };
                                            requestQueue4.getCache().clear();
                                            requestQueue4.add(request2);



                                        }
                                        else  if(sparseBooleanArray.get(i) == false)
                                        {
  /*METER ALUMNOS ASISTIDOS*/                                        unchecked+= getTagi1.toString() +"." + "\t";

                                            StringRequest request2 = new StringRequest(Request.Method.POST, insertAlumnPL, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Log.d(TAG, response.toString());
                                                    try {

                                                        JSONObject jsonObject= new JSONObject(response);
                                                        JSONArray jsonArray = jsonObject.getJSONArray("listaAlumno");

                                                        JSONObject object = jsonArray.getJSONObject(0);
                                                        String resultadoInsrtAl = object.getString("CLAVE");
                                                        if(resultadoInsrtAl != "0"){
                                                            System.out.println("Se inserto correctamente a: "+getTagi1.toString());
                                                        }else{

                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
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
                                                    Map<String, String> parametros2 = new HashMap<String, String>();;
                                                    parametros2.put("alumny", getTagi1.toString());
                                                    parametros2.put("statusy", "FALTA");
                                                    return parametros2;

                                                }

                                            };
                                            requestQueue5.getCache().clear();
                                            requestQueue5.add(request2);

                                        }

                                    }
                                    System.out.println("NO CHECADOS -- "+unchecked);
                                    Toast.makeText(getActivity(), "CHECADOS -- "+checked, Toast.LENGTH_SHORT).show();
                                    getActivity().getFragmentManager().popBackStack();

                                }else{
                                    System.out.println("LOS DATOS DE LA CLASE NO SE SUBIERON");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
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
                        Map<String, String> parametros2 = new HashMap<String, String>();;
                        parametros2.put("classy", chossenIdClassToDB);
                        parametros2.put("daty", chossenDateToDB);
                        parametros2.put("houry", chossenTimeToDB);
                        parametros2.put("profy", idProfesrSess);
                        return parametros2;

                    }

                };
                requestQueue3.getCache().clear();
                //request2.setShouldCache(false);
                requestQueue3.add(request4);

            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        final Calendar c = Calendar.getInstance();

                        final int mes = c.get(Calendar.MONTH);
                        final int dia = c.get(Calendar.DAY_OF_MONTH);
                        final int anio = c.get(Calendar.YEAR);

                        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                int mesActual = month + 1;
                                String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                                String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);

                                String chossenDate =year + "/" + mesFormateado +"/"+ diaFormateado;
                                chossenDateToDB =year + "-" + mesFormateado +"-"+ diaFormateado;

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                try {
                                    finaldate = sdf.parse(chossenDate);
                                    System.out.println("FECHA ELEGIDA "+chossenDate);
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
                                Date now = finaldate;
                                et.setText(df.format(now));

                            }
                        },anio, mes, dia);

                        recogerFecha.show();


            }
        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // showMyDialog();
                }
            }
        });



        consultClass();
        spiny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringWithTag2 s = (StringWithTag2) parent.getItemAtPosition(position);
                final Object tag = s.tag;
                String label = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "You selected: " + tag,Toast.LENGTH_LONG).show();
                if(!listAlumn.equals(null)){
                    System.out.println("Ya esta lleno, se debe vaciar");
                    System.out.println(listAlumn.toString());
                    listk.invalidateViews();
                    listk.refreshDrawableState();
                }

                if(tag.equals(null)){
                    System.out.println("no hay id seleccionado");
                }else{
                    chossenIdClassToDB=tag.toString();

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

                                    listAlumn.add(new StringWithTag2(object.getString("nombreCompleto"), object.getString("idUsuario")));

                                    System.out.println(nombreCompleto + idUsuario);

                                    if (listAlumn.size() == 0) {
                                        System.out.println("Esta vacio");
                                    } else {
                                        //System.out.println(Arrays.toString(ListData));
                                        startAttendance.setEnabled(true);
                                        checkList();
                                    }
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == CHECK_LIST) {
                if (resultCode == RESULT_OK) {
                    currentCheckyID = intent.getStringExtra("currentChecky");
                    System.out.println("CURRENTCHECKY " + currentCheckyID);

                    if (currentCheckyID == null) {

                    } else {
                        startAttendance.setText("CONTINUAR ESCANEANDO");
                        finishAttendance.setVisibility(View.VISIBLE);
                        System.out.println("TOTAL DE LA LISTA: "+listk.getAdapter().getCount());

                        Integer count=0;
                        while(count != listk.getAdapter().getCount()) {
                            StringWithTag2 getStri = (StringWithTag2) listk.getItemAtPosition(count);
                            Object getTagi = getStri.tag;
                            if(getTagi.toString().equals(currentCheckyID)){
                                System.out.println("SIIIII COINCIIDEEN");
                                listk.setItemChecked(count,true);
                            }else{
                            }
                            count++;
                        }






                    }
                }

        }
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
                        idClase = object.getString("IDCLASE");
                        String materia = object.getString("NOMBREMATERIA");
                        String dia = object.getString("DIA");
                        try {
                            int dayOfWeek = parseDayOfWeek(dia, Locale.US);
                            bg = days_dias[dayOfWeek];
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String horaEntrada = object.getString("HORAENTRADA");
                        String grup = object.getString("GRUPO");
                        if(materia.length() <= 10) {

                        }else{
                            if(materia.length()>40){
                                materia = materia.substring(0,(materia.length() /2)-10 );
                            }else{
                            materia = materia.substring(0, materia.length() /2);}
                        }

                        list.add(new StringWithTag2( materia+"... - "+bg+" - "+horaEntrada,object.getString("IDCLASE")));
                        //ListData2[i] = object.getString("GRUPO");


                    }
                    if(list.size() == 0){
                        System.out.println("Esta vacio");
                    }else{
                        System.out.println(list.toString());

                        if (getActivity() == null) {
                        }else {
                            ArrayAdapter<StringWithTag2> adap = new ArrayAdapter<StringWithTag2>(getActivity(), android.R.layout.simple_spinner_item, list);
                            adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spiny.setAdapter(adap);
                        }
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
        requestQueue.getCache().clear();
        requestQueue.add(request);
    }

    private static int parseDayOfWeek(String day, Locale locale)
            throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("E", locale);
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
    public void checkList() {
        ArrayAdapter<StringWithTag2> adaptador2= new ArrayAdapter<StringWithTag2>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_checked,android.R.id.text1,listAlumn){
            @Override
            public boolean isEnabled(int position) {
                return false;
            }

        };

        adaptador2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        listk.setAdapter(adaptador2);

    }


    public class StringWithTag2 {
        public String string;
        public Object tag;

        public StringWithTag2(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }



        @Override
        public String toString() {
            return string;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void onDestroy() {
        super.onDestroy();



    }

}
