package com.example.asistcupmh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.asistcupmh.Bienvenida;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Attendance extends AppCompatActivity {
    private static final String TAG = "OCVSample::Activity";
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    double maxRatio = 0.19
            ;
    String bestFileName = null;
    String bestFileId = null;
    AlertDialog dialog;
    public ProgressBar progressi;
    Button buti;
    private int progressStatus = 0;
    public  TextView textView,textView2;
    private ImageView displayFingi;
    private Handler handler = new Handler();
    private final int SCAN_FINGER = 0;
    byte[] img, img2;
    public Bitmap bmpImg, bmpImg2, bm, bm2;
    String[] currentCheckyList=new String[0];
    public String currentChecky=null;
    String showUrl="https://alixmpledcwam.000webhostapp.com/practica1/mostrar.php";
    RequestQueue requestQueue;
    private  ImageButton itsCorrect;
    private ImageButton itsIncorrect;
    private Context BienvenidaActivity;
    String idClass="";

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        itsCorrect=(ImageButton) findViewById(R.id.itsOk);
        itsIncorrect=(ImageButton) findViewById(R.id.itsBad);
        buti=(Button) findViewById(R.id.checkmyF);
        textView=(TextView) findViewById(R.id.textView3);
        textView2=(TextView) findViewById(R.id.textView5);
        displayFingi=(ImageView) findViewById(R.id.imageFin);
        progressi =(ProgressBar) findViewById(R.id.progressBar2);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            String h = extras.getString("IDCLASS");
            idClass = h;
            System.out.println("LLEGO EL ENVIO DE IDCLASS "+h);
        }else{
            System.out.println("NO LLEGO EL ENVIO DE IDCLASS");
        }
        //progressi.getIndeterminateDrawable().setColorFilter(Color.parseColor("#00BCD4"), android.graphics.PorterDuff.Mode.MULTIPLY);

        itsCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bestFileId==null){

                }else {
                    Intent data = new Intent();
                    data.putExtra("currentChecky", bestFileId);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        itsIncorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(RESULT_CANCELED, data);
                finish();

            }
        });


    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }

    public void checkFing(View view) {
        progressi.setVisibility(View.VISIBLE);
        new MyTask().execute();
    }


    public class MyTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(1000);
                checkTheFinger();
                //publishProgress(ImageProcessing.progressStep);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }
        public void doProgress(int value){
            publishProgress(value);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressi.setVisibility(View.GONE);
            itsCorrect.setVisibility(View.VISIBLE);
            itsIncorrect.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPreExecute() {
            textView2.setVisibility(View.VISIBLE);
            textView2.setText("Esto puede tardar unos minutos...");
            buti.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            textView2.setText("Buscando..."+ values[0]+"%");
            progressi.setProgress(values[0]);
        }
    }

    public void checkTheFinger() {
        Bitmap checkyBitmap;
        displayFingi.buildDrawingCache();
        checkyBitmap = displayFingi.getDrawingCache();
        if(checkyBitmap == null){
            System.out.println("no hay nada");
        }
        Bitmap finalBitmap=checkKeyPoints(checkyBitmap);
        System.out.println("Minutiae: "+ String.valueOf(ImageProcessing.numberMinutae));


        StringRequest request = new StringRequest(Request.Method.POST, showUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    MatOfKeyPoint keypointsBase = ImageProcessing.getKeypoints();
                    Mat descriptorsBase = ImageProcessing.getDescriptors();
                    Integer numMinutiae=ImageProcessing.getMinutae();

                    JSONObject jsonObject= new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("FingerTemplate");
                    Integer fingerMintae;
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject object0 = jsonArray.getJSONObject(i);
                        String fingerTmpId = object0.getString("idUsuario");
                        String fingerTmpN = object0.getString("nombreCompleto");
                        String fingerTmpK = object0.getString("finger_kp1");
                        String fingerTmpD = object0.getString("finger_dp1");
                        if(object0.getString("finger_mn1")=="null"){
                            fingerMintae =0;
                        }else{
                            fingerMintae = Integer.valueOf(object0.getString("finger_mn1"));
                        }
                        if(fingerTmpK == "null"&&fingerTmpD == "null"){
                            //System.out.println("Keypoints & descriptors NULL");
                        }else {
                            Mat descriptorsToMatch = jsonToMat(fingerTmpD);
                            MatOfKeyPoint keypointsToMatch = jsonToKeypoints(fingerTmpK);
                            double distancy = ImageProcessing.matchFeatures(keypointsBase,descriptorsBase,keypointsToMatch, descriptorsToMatch);
                            System.out.println("Nombre: "+fingerTmpN+" Distancia: " + distancy);

                            double ratio = distancy;
                            if (ratio > maxRatio) {
                                if(ratio!=0){

                                    maxRatio = ratio;
                                    bestFileId =  fingerTmpId;
                                    bestFileName = fingerTmpN;
                                }else{

                                }

                            }else{
                                System.out.println("Ratio= "+ratio);
                            }

                        }

                    }if(bestFileName!=null) {
                        textView.setText("**** Se encontro una coincidencia ****");
                        textView2.setText("Nombre encontrado: " + String.format("%s: %s%%", bestFileName, (int) (((maxRatio*3)+0.10) * 100)));
                    }else{
                        textView.setText("**** No se encontraron coincidencias ****");
                        textView2.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                final Toast toast = Toast.makeText(Attendance.this, error.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();;
                parametros.put("idClass", idClass);
                return parametros;

            }

        };
        requestQueue.getCache().clear();
        requestQueue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        displayFingi.setImageResource(0);
        int status;
        String errorMesssage;
        switch(requestCode) {
            case (SCAN_FINGER) : {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        Toast.makeText(Attendance.this, "Fingerprint captured", Toast.LENGTH_SHORT).show();
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        displayFingi.setImageBitmap(bm);
                        displayFingi.setEnabled(false);
                        buti.setVisibility(View.VISIBLE);
                        textView.setText(" ");
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        Toast.makeText(Attendance.this, errorMesssage , Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }
    public Bitmap checkKeyPoints(Bitmap bitmap) {
        ImageProcessing p = new ImageProcessing(bitmap, Attendance.this);
        return p.getProcessedImage();
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

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if ( dialog!=null && dialog.isShowing() ){
            dialog.cancel();
        }

    }

}
