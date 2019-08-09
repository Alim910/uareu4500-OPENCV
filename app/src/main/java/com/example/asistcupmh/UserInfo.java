package com.example.asistcupmh;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserInfo extends Fragment {
    static String DATA_RECEIVE = "data_receive";
    static String DATA_ID_RECEIVE = "data_id_receive";
    String idAlumn=null;
    private static final String LOG_TAG = "U.are.U-Image";
    ImageView imvUser,statyscany;
    EditText fingerName,fingerData,keypointdata,descriptordata;
    TextView logi,logid;
    Button saveTemplate;
    byte[] img,imgFingerprint;
    public Bitmap bm, bm2;
    private static final int SCAN_FINGER = 0;
    String encodedImage;
    RequestQueue requestQueue,requestQueue2;
    String infoUrl = "https://alixmpledcwam.000webhostapp.com/practica1/infoUser.php";
    String insertUrl = "https://alixmpledcwam.000webhostapp.com/practica1/insertar.php";
    private static final String TAG = "OCVSample::Activity";
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    public static double MatchingThreshold = 45;
    Integer numMinutae;
    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeOpenCVDependencies() throws IOException {
        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_userinfo,container,false);
        saveTemplate= (Button) rootView.findViewById(R.id.btnSave);
        statyscany= (ImageView) rootView.findViewById(R.id.starscany);
        imvUser = (ImageView) rootView.findViewById(R.id.imgUser);
        fingerName = (EditText) rootView.findViewById(R.id.name);
        fingerData = (EditText) rootView.findViewById(R.id.fng1);
        keypointdata = (EditText) rootView.findViewById(R.id.kp1);
        descriptordata = (EditText) rootView.findViewById(R.id.dscp1);
        logi=(TextView) rootView.findViewById(R.id.namy);
        logid=(TextView) rootView.findViewById(R.id.idy);
        requestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue2= Volley.newRequestQueue(getActivity().getApplicationContext());

        statyscany.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivityForResult(intent, SCAN_FINGER);
            }
        });

        saveTemplate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bitmap checkyBitmap;
                statyscany.buildDrawingCache();
                checkyBitmap = statyscany.getDrawingCache();
                if(checkyBitmap == null){
                    Toast.makeText(getContext(), "No hay nada...", Toast.LENGTH_SHORT).show();
                }
                if(keypointdata.getText().toString().trim().length() == 0 || descriptordata.getText().toString().trim().length() == 0) {
                    checkKeyPoints(checkyBitmap);
                    MatOfKeyPoint keypoints = ImageProcessing.getKeypoints();
                    Mat descriptors = ImageProcessing.getDescriptors();
                    numMinutae = ImageProcessing.getMinutae();
                    String keypointsJSON = keypointsToJSON(keypoints);
                    String descriptorsJSON = matToJSON(descriptors);
                    keypointdata.setText(keypointsJSON);
                    descriptordata.setText(descriptorsJSON);
                }

                StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "Se ha guardado con exito", Toast.LENGTH_SHORT).show();
                        System.out.println(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NetworkError) {
                        } else if (error instanceof ServerError) {
                        } else if (error instanceof AuthFailureError) {
                        } else if (error instanceof ParseError) {
                        } else if (error instanceof NoConnectionError) {
                        } else if (error instanceof TimeoutError) {
                            Toast.makeText(getContext(),
                                    "Oops. Timeout error!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Bundle args = getArguments();
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.get("FINGERPARAMETERS");
                        parametros.put("namePerson", logid.getText().toString());
                        parametros.put("fingerTmp1", fingerData.getText().toString());
                        parametros.put("fngkeypoints", keypointdata.getText().toString());
                        parametros.put("fngdescrptrs", descriptordata.getText().toString());
                        parametros.put("fngnominutiae", String.valueOf(numMinutae));


                        return parametros;

                    }

                };
                requestQueue.add(request);
            }
        });

        return rootView;
    }

    public Bitmap checkKeyPoints(Bitmap bitmap) {
        Toast.makeText(getContext(), "Probando...", Toast.LENGTH_SHORT).show();
        ImageProcessing p = new ImageProcessing(bitmap, getActivity());
        return p.getProcessedImage();
    }
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            logi.setText(args.getString(DATA_RECEIVE));
            idAlumn=args.getString(DATA_ID_RECEIVE);
            logid.setText(args.getString(DATA_ID_RECEIVE));
            StringRequest request2 = new StringRequest(Request.Method.POST, infoUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("1")){
                        try {

                            JSONObject jsonObject= new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("information");

                            JSONObject object = jsonArray.getJSONObject(0);
                            String idUsuario = object.getString("MATRICULA");
                            String nombreCompleto = object.getString("NOMBRE");
                            String rol = object.getString("ROL");
                            String foto = object.getString("FOTO");
                            String fotoFP = object.getString("FINGER");
                            if(object.getString("FOTO")!= "null"){
                                new DownloadImageTask(imvUser)
                                        .execute("http://alixmpledcwam.000webhostapp.com/resrvlabs2019/"+foto);

                            }else{
                                System.out.println("FOTO VACIA");
                            }
                            if(fotoFP != "null"){
                                byte[] b = Base64.decode(fotoFP.getBytes(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                                if(bm!=null){
                                    statyscany.setImageBitmap(bm);
                                }else {
                                    statyscany.setImageBitmap(bitmap);
                                }

                            }else{
                                System.out.println("FINGERPRINT VACIA");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //Toast.makeText(getActivity(),"Datos incorrectos",Toast.LENGTH_SHORT).show();
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
                    parametros2.put("usuarioo", idAlumn);
                    return parametros2;

                }

            };
            requestQueue2.getCache().clear();
            //request2.setShouldCache(false);
            requestQueue2.add(request2);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        statyscany.setImageResource(0);
        int status;
        String errorMesssage;
        switch(requestCode) {
            case (SCAN_FINGER) : {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        Toast.makeText(getContext(), "Fingerprint captured", Toast.LENGTH_SHORT).show();
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        statyscany.setImageBitmap(bm);
                        statyscany.setEnabled(false);
                        encodedImage = Base64.encodeToString(img, Base64.DEFAULT);
                        fingerData.setText(encodedImage);
                        saveTemplate.setEnabled(true);
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        Toast.makeText(getContext(), errorMesssage , Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }



    public String keypointsToJSON(MatOfKeyPoint kps){
        Gson gson = new Gson();

        JsonArray jsonArr = new JsonArray();

        KeyPoint[] kpsArray = kps.toArray();
        for(KeyPoint kp : kpsArray){
            JsonObject obj = new JsonObject();

            obj.addProperty("class_id", kp.class_id);
            obj.addProperty("x", kp.pt.x);
            obj.addProperty("y", kp.pt.y);
            obj.addProperty("size", kp.size);
            obj.addProperty("angle", kp.angle);
            obj.addProperty("octave", kp.octave);
            obj.addProperty("response", kp.response);

            jsonArr.add(obj);
        }

        return gson.toJson(jsonArr);
    }


    public static String matToJSON(Mat mat){
        JsonObject obj = new JsonObject();

        int cols = mat.cols();
        int rows = mat.rows();
        int elemSize = (int) mat.elemSize();

        byte[] data = new byte[cols * rows * elemSize];

        mat.get(0, 0, data);

        obj.addProperty("rows", mat.rows());
        obj.addProperty("cols", mat.cols());
        obj.addProperty("type", mat.type());

        String dataString = new String(Base64.encode(data, Base64.DEFAULT));

        obj.addProperty("data", dataString);

        Gson gson = new Gson();

        return gson.toJson(obj);
    }


    public static MatOfKeyPoint jsonToKeypoints(String json){
        MatOfKeyPoint result = new MatOfKeyPoint();

        JsonParser parser = new JsonParser();
        JsonArray jsonArr = parser.parse(json).getAsJsonArray();

        int size = jsonArr.size();

        KeyPoint[] kpArray = new KeyPoint[size];

        for(int i=0; i<size; i++){
            KeyPoint kp = new KeyPoint();

            JsonObject obj = (JsonObject) jsonArr.get(i);

            kp.pt = new Point(
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble()
            );
            kp.class_id = obj.get("class_id").getAsInt();
            kp.size = obj.get("size").getAsFloat();
            kp.angle = obj.get("angle").getAsFloat();
            kp.octave = obj.get("octave").getAsInt();
            kp.response = obj.get("response").getAsFloat();

            kpArray[i] = kp;
        }

        result.fromArray(kpArray);

        return result;
    }


    public static Mat jsonToMat(String json){
        JsonParser parser = new JsonParser();
        JsonObject JsonObject = parser.parse(json).getAsJsonObject();

        int rows = JsonObject.get("rows").getAsInt();
        int cols = JsonObject.get("cols").getAsInt();
        int type = JsonObject.get("type").getAsInt();

        System.out.println("ROWS: "+rows+" COLS: "+cols);

        String dataString = JsonObject.get("data").getAsString();
        byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);
        System.out.println(data);

        Mat mat = new Mat(rows, cols, type);
        mat.put(0, 0, data);

        return mat;
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
            bmImage.setImageBitmap(result);
        }
    }

}