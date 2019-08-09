package com.example.asistcupmh;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "U.are.U-Image";
    ImageView ivFinger, ivFinger2, ivFinger3, ivFinger4;
    EditText fingerName,fingerData,keypointdata,descriptordata;
    TextView logi;
    byte[] img, img2;
    public Bitmap bmpImg, bmpImg2, bm, bm2;
    private static final int SCAN_FINGER = 0;
    String encodedImage;
    RequestQueue requestQueue;
    String insertUrl = "http://alixmpledcwam.000webhostapp.com/practica1/insertar.php";
    String showUrl="http://alixmpledcwam.000webhostapp.com/practica1/mostrar.php";
    private static final String TAG = "OCVSample::Activity";
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    public static double MatchingThreshold = 45;
    private ArrayList<Bitmap> matchResults;
    double maxRatio = 0;
    String bestFileName = null;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logi = (TextView) findViewById(R.id.LOG);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        ivFinger2 = (ImageView) findViewById(R.id.ivFingerDisplay2);
        ivFinger3 = (ImageView) findViewById(R.id.ivFingerDisplay3);
        ivFinger4 = (ImageView) findViewById(R.id.ivFingerDisplay4);
        fingerName = (EditText) findViewById(R.id.name);
        fingerData = (EditText) findViewById(R.id.fng1);
        keypointdata = (EditText) findViewById(R.id.kp1);
        descriptordata = (EditText) findViewById(R.id.dscp1);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

    }

    public void startScan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ivFinger.setImageResource(0);
        ivFinger2.setImageResource(0);
        int status;
        String errorMesssage;
        switch(requestCode) {
            case (SCAN_FINGER) : {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        Toast.makeText(MainActivity.this, "Fingerprint captured", Toast.LENGTH_SHORT).show();
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ivFinger.setImageBitmap(bm);
                        encodedImage = Base64.encodeToString(img, Base64.DEFAULT);
                        fingerData.setText(encodedImage);
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        Toast.makeText(MainActivity.this, errorMesssage , Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }

        }
    }

    public void checkFing(View view) {
        //if (bmpImg != null) {

        //  bmpImg.recycle();
        //  bmpImg.eraseColor(Color.TRANSPARENT);
        // }
        Bitmap checkyBitmap;
        ivFinger.buildDrawingCache();
        checkyBitmap = ivFinger.getDrawingCache();
        if(checkyBitmap == null){
            Toast.makeText(MainActivity.this, "No hay nada...", Toast.LENGTH_SHORT).show();
        }
        Bitmap finalBitmap=checkKeyPoints(checkyBitmap);
        ivFinger2.setImageBitmap(finalBitmap);
        logi.setText("Minutiae: "+ String.valueOf(ImageProcessing.numberMinutae));
        JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response0) {
                try {
                    Mat skeleton=new Mat();
                    JSONArray FingersArray = response0.getJSONArray("FingerTemplate");


                    MatOfKeyPoint keypointsBase = ImageProcessing.getKeypoints();
                    Mat descriptorsBase = ImageProcessing.getDescriptors();


                    for (int i=0; i<FingersArray.length(); i++) {
                        JSONObject object0 = FingersArray.getJSONObject(i);
                        String fingerTmpN = object0.getString("nombreusuario");
                        String fingerTmpK = object0.getString("finger_kp1");
                        String fingerTmpD = object0.getString("finger_dp1");
                        Mat descriptorsToMatch = jsonToMat(fingerTmpD);
                        MatOfKeyPoint keypointsToMatch = jsonToKeypoints(fingerTmpK);

                        double distancy = ImageProcessing.matchFeatures(keypointsBase,descriptorsBase,keypointsToMatch, descriptorsToMatch);
                        System.out.println("Nombre: "+fingerTmpN+" Distancia: " + distancy);

                        double ratio = distancy;
                        if (ratio > maxRatio) {
                            maxRatio = ratio;
                            bestFileName = fingerTmpN;
                        }else{
                            System.out.println("Ratio= "+ratio);
                        }

                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Match found");
                    builder.setMessage(String.format("%s: %s%%", bestFileName, (int) (maxRatio * 100)));
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();

                    // Must call show() prior to fetching text view
                    TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);



                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hubo un error en la conexion", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest0);

    }
    public Bitmap checkKeyPoints(Bitmap bitmap) {
        Toast.makeText(MainActivity.this, "Probando...", Toast.LENGTH_SHORT).show();
        ImageProcessing p = new ImageProcessing(bitmap, MainActivity.this);
        return p.getProcessedImage();
    }

    public void saveTemplate(View view) throws FileNotFoundException {
        if (fingerName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Ingrese el nombre de su huella", Toast.LENGTH_SHORT).show();
        } else {

            MatOfKeyPoint keypoints = ImageProcessing.getKeypoints();
            Mat descriptors = ImageProcessing.getDescriptors();
            String keypointsJSON = keypointsToJSON(keypoints);
            String descriptorsJSON = matToJSON(descriptors);
            keypointdata.setText(keypointsJSON);
            descriptordata.setText(descriptorsJSON);

            StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(MainActivity.this, "Se ha registrado con exito", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Hubo un error al registrar, intentelo mas tarde", Toast.LENGTH_SHORT).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.get("FINGERPARAMETERS");
                    parametros.put("namePerson", fingerName.getText().toString());
                    parametros.put("fingerTmp1", fingerData.getText().toString());
                    parametros.put("fngkeypoints", keypointdata.getText().toString());
                    parametros.put("fngdescrptrs", descriptordata.getText().toString());


                    return parametros;

                }

            };
            requestQueue.add(request);
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

    public void showFinger(View view) {
        JsonObjectRequest jsonObjectRequest0 = new JsonObjectRequest(Request.Method.POST, showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response0) {
                try {
                    Mat skeleton=new Mat();
                    JSONArray FingersArray = response0.getJSONArray("FingerTemplate");


                    MatOfKeyPoint keypointsBase = ImageProcessing.getKeypoints();
                    Mat descriptorsBase = ImageProcessing.getDescriptors();


                    for (int i=0; i<FingersArray.length(); i++) {
                        JSONObject object0 = FingersArray.getJSONObject(i);
                        String fingerTmpN = object0.getString("nombreusuario");
                        String fingerTmpK = object0.getString("finger_kp1");
                        String fingerTmpD = object0.getString("finger_dp1");
                        Mat descriptorsToMatch = jsonToMat(fingerTmpD);
                        MatOfKeyPoint keypointsToMatch = jsonToKeypoints(fingerTmpK);

                        double distancy = ImageProcessing.matchFeatures(keypointsBase,descriptorsBase,keypointsToMatch, descriptorsToMatch);
                        System.out.println("Nombre: "+fingerTmpN+" Distancia: " + distancy);

                        double ratio = distancy;
                        if (ratio > maxRatio) {
                            maxRatio = ratio;
                            bestFileName = fingerTmpN;
                        }else{
                            System.out.println("Ratio= "+ratio);
                        }

                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Match found");
                    builder.setMessage(String.format("%s: %s%%", bestFileName, (int) (maxRatio * 100)));
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.show();

                    // Must call show() prior to fetching text view
                    TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER);



                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hubo un error en la conexion", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest0);


    }



    private Bitmap mat2Bitmap(Mat src, int code) {
        Mat rgbaMat = new Mat(src.width(), src.height(), CvType.CV_8UC4);
        Imgproc.cvtColor(src, rgbaMat, code, 4);
        Bitmap bmp = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgbaMat, bmp);
        return bmp;
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


    }


}