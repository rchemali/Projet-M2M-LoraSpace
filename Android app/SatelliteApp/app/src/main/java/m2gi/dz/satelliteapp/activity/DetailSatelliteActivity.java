package m2gi.dz.satelliteapp.activity;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import m2gi.dz.satelliteapp.R;
import m2gi.dz.satelliteapp.adapter.RecyclerItemClickListener;
import m2gi.dz.satelliteapp.adapter.SatelitteAdapter;
import m2gi.dz.satelliteapp.model.Satellite;

public class DetailSatelliteActivity extends AppCompatActivity implements MqttCallback{

    String id = "";
    String name = "";
    TextView txtname, txtlat, txtlng,txtelev,txtazimu,txtaltitude;
    Button btnSend ,btnStop;
    String azimuth = "";

    Thread thread ;

    MqttAndroidClient client ;
    final String topicName = "m2m-loraspace/data/gps/name";
    final String topicCord = "m2m-loraspace/config/servos";
    final String topicLatitude ="m2m-loraspace/data/gps/latitude";
    final String topicLongitude = "m2m-loraspace/data/gps/longitude";
    final String topicAltitude = "m2m-loraspace/data/gps/alt";



    String coordinate = "";
    int qos = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_satellite);

        txtname=(TextView)findViewById(R.id.name);
        txtlat=(TextView)findViewById(R.id.lat);
        txtlng=(TextView)findViewById(R.id.lng);
        txtelev=(TextView)findViewById(R.id.elevation);
        txtazimu=(TextView)findViewById(R.id.azimuth);
        txtaltitude=(TextView)findViewById(R.id.altitude);
        btnSend = (Button)findViewById(R.id.sendNode);
        btnStop =(Button)findViewById(R.id.stop);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name =intent.getStringExtra("name");

        initMqtt();
        getSatelliteInformation();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    publishMessage(client,name,1,topicName);
                    try {
                        publishMessage(client,"45.193608",1,topicLatitude);
                        publishMessage(client,"5.764625",1,topicLongitude);
                        publishMessage(client,"200",1,topicAltitude);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                   runTracking();
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    thread.stop();
                }catch (Exception e){

                }

            }
        });

        txtname.setText(name);



      /*  (new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run()
                            {
                                getSatelliteInformation();
                               try {
                                    publishMessage(client,coordinate,1,topicCord);
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
                    }
                    catch (InterruptedException e)
                    {

                    }
            }
        })).start(); */


    }

    public void runTracking(){

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(15000);
                        runOnUiThread(new Runnable() // start actions in UI thread
                        {

                            @Override
                            public void run()
                            {
                                getSatelliteInformation();
                                try {
                                    publishMessage(client,coordinate,1,topicCord);
                                    Toast.makeText(getApplication(),"sended",Toast.LENGTH_LONG).show();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
                    }
                    catch (InterruptedException e)
                    {

                    }
            }
        });

        thread.start();

    }

    public void getSatelliteInformation(){


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET,"https://www.n2yo.com/rest/v1/satellite/positions/" +id+"/45/5/200/1&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {

                            JSONArray array = response.getJSONArray("positions");


                            for(int i = 0 ; i<array.length();i++){

                                JSONObject object = array.getJSONObject(i);

                                coordinate = object.toString();

                                txtaltitude.setText("ALTITUDE :"+object.getString("sataltitude"));
                                txtlat.setText("Latitude:"+object.getString("satlatitude"));
                                txtlng.setText("Longitude :"+object.getString("satlongitude"));
                                txtelev.setText("Elevation :"+object.getString("elevation"));
                                txtazimu.setText("Azimuth :"+object.getString("azimuth"));

                                azimuth = object.getString("azimuth");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        Volley.newRequestQueue(this).add(jsonRequest);



    }

    public void initMqtt(){

        //MQTTConnect options : setting version to MQTT 3.1.1
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        //Below code binds MainActivity to Paho Android Service via provided MqttAndroidClient
        // client interface
        //Todo : Check why it wasn't connecting to test.mosquitto.org. Isn't that a public broker.
        //Todo : .check why client.subscribe was throwing NullPointerException  even on doing subToken.waitForCompletion()  for Async                  connection estabishment. and why it worked on subscribing from within client.connect’s onSuccess(). SO
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt connect", "onSuccess");
                    Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_SHORT).show();
                    //Subscribing to a topic door/status on broker.hivemq.com
                    client.setCallback(DetailSatelliteActivity.this);

                    try {
                        IMqttToken subToken = client.subscribe(topicCord, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                Toast.makeText(getApplicationContext(), "Successfully subscribed to: " + topicName, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(getApplicationContext(), "Couldn't subscribe to: " + topicName, Toast.LENGTH_SHORT).show();

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("failed co", "onFailure");
                    Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    public void publishMessage(@NonNull MqttAndroidClient client,
                               @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setId(5866);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

     //   Toast.makeText(getApplicationContext(),message.toString(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
