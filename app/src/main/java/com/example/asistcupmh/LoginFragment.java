package com.example.asistcupmh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;


public class LoginFragment extends Fragment {
    ImageView userImage;
    Button login,edit;
    EditText idUser,idpassword;
    String acessUrl = "https://alixmpledcwam.000webhostapp.com/practica1/accesoprofes.php";
    RequestQueue requestQueue2;
    TextView message;
    private Session session;
    private View rootView1;
    String idUsuario;
    String nombreCompleto;
    String rol;
    String foto;
    RoundedBitmapDrawable roundedBitmapDrawable;


    @Override
    public void onViewStateRestored(Bundle inState) {
        super.onViewStateRestored(inState);
        if (inState != null) {
            idUsuario = inState.getString("nombreCompleto");
            nombreCompleto = inState.getString("nombreCompleto");
            foto = inState.getString("foto");
            System.out.println("CURRENT FOTO OF PROFF"+foto);
            idUser.setVisibility(View.GONE);
            idpassword.setVisibility(View.GONE);
            message.setText("Bienvenido "+nombreCompleto+"!");
            login.setText("CERRAR SESION");

        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView1=inflater.inflate(R.layout.fragment_login,container,false);
        userImage=(ImageView)rootView1.findViewById(R.id.imgUser);
        message=(TextView) rootView1.findViewById(R.id.txtMessage);
        idUser=(EditText) rootView1.findViewById(R.id.edTxtId);
        idpassword=(EditText) rootView1.findViewById(R.id.edTxtPass);
        login=(Button) rootView1.findViewById(R.id.btnLog);
        requestQueue2= Volley.newRequestQueue(getActivity().getApplicationContext());
        session = new Session(getActivity().getApplicationContext());


        if(!session.getid().isEmpty()||!session.getusename().isEmpty()||!session.getfoto().isEmpty()){
            System.out.println("LAS VARIABLES ESTAN LLENAS");
            idUser.setVisibility(View.GONE);
            idpassword.setVisibility(View.GONE);
            message.setText("Bienvenido "+session.getusename()+"!");
                if(session.getfoto()!=null){
                    if(getResources()==null){

                    }else {
                        new DownloadImageTask(userImage)
                                .execute("http://alixmpledcwam.000webhostapp.com/resrvlabs2019/" + session.getfoto());
                    }

                }
            login.setText("CERRAR SESION");
        }

        Bitmap bity =BitmapFactory.decodeResource(getResources(),R.drawable.user_unknown);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bity);
        roundedBitmapDrawable.setCircular(true);
        userImage.setImageDrawable(roundedBitmapDrawable);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(login.getText().toString()=="CERRAR SESION"){
                    userImage.setImageDrawable(roundedBitmapDrawable);
                    session.dropAll(getContext());
                    idUser.setVisibility(View.VISIBLE);
                    idpassword.setVisibility(View.VISIBLE);
                    message.setText("Hola! Inicia sesión");
                    login.setText("INICIAR SESION");

                } else{
                StringRequest request2 = new StringRequest(Request.Method.POST, acessUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
if(response.contains("1")){
    try {

        JSONObject jsonObject= new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("acceso");

            JSONObject object = jsonArray.getJSONObject(0);
            idUsuario = object.getString("MATRICULA");
            nombreCompleto = object.getString("NOMBRE");
            rol = object.getString("ROL");
            foto = object.getString("FOTO");
            if(object.getString("ROL").equals("Docente")){
                if(!object.getString("FOTO").equals(null)){
                    if(getResources()==null){

                    }else {
                        new DownloadImageTask(userImage)
                                .execute("http://alixmpledcwam.000webhostapp.com/resrvlabs2019/" + foto);
                    }
                }
                Toast.makeText(getActivity(),"Ingreso correcto",Toast.LENGTH_SHORT).show();
                idUser.setVisibility(View.GONE);
                idpassword.setVisibility(View.GONE);
                message.setText("Bienvenido "+nombreCompleto+"!");
                login.setText("CERRAR SESION");
                session.setid(idUsuario);
                session.setusename(nombreCompleto);
                session.setfoto(foto);
            }else{
                Toast.makeText(getActivity(),"No es profesor",Toast.LENGTH_SHORT).show();
            }


    } catch (JSONException e) {
        e.printStackTrace();
        Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
    }
}else{
    Toast.makeText(getActivity(),"Datos incorrectos",Toast.LENGTH_SHORT).show();
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
                        parametros2.put("usuarioo", idUser.getText().toString());
                        parametros2.put("contra", idpassword.getText().toString());
                        return parametros2;

                    }

                };
                requestQueue2.getCache().clear();
                //request2.setShouldCache(false);
                requestQueue2.add(request2);
                }


            }
        });
        return rootView1;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),result);
            roundedBitmapDrawable.setCircular(true);
            bmImage.setImageDrawable(roundedBitmapDrawable);
           // bmImage.setImageBitmap(result);
        }

}



    @Override
    public void onPause() {
        super.onPause();

    }



}
