package m2gi.dz.satelliteapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

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
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import m2gi.dz.satelliteapp.R;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    EditText editAbove;
    Button btnSend;
    AsyncHttpClient client = new AsyncHttpClient();
    double latitude , longitude , altitude =0.0 ;
    TextView txtlatitude,txtlongitude;
    NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtlatitude = (TextView)findViewById(R.id.latitude);
        txtlongitude = (TextView)findViewById(R.id.longitude);
        editAbove = (EditText)findViewById(R.id.editAbove);
        btnSend = (Button)findViewById(R.id.button);
        formatter = new DecimalFormat("#0.000");

        initMqtt();

        txtlatitude.setText("45.193608");
        txtlongitude.setText("5.764625");


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ongle = editAbove.getText().toString();

                Intent intent = new Intent(MainActivity.this,ListSatelliteActivity.class);
                intent.putExtra("ongle",ongle);
                intent.putExtra("longitude",String.valueOf(longitude));
                intent.putExtra("latitude",String.valueOf(latitude));
                intent.putExtra("altitude",String.valueOf(altitude));

                startActivity(intent);


            }
        });


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
        final MqttAndroidClient client =
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
                    client.setCallback(MainActivity.this);
                    final String topic = "m2m-loraspace/data/gps";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                Toast.makeText(getApplicationContext(), "Successfully subscribed to: " + topic, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(getApplicationContext(), "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();

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

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String tabGps[] = message.toString().split("#");
        latitude = Double.valueOf(tabGps[0]);
        longitude = Double.valueOf(tabGps[1]);
        altitude = Double.valueOf(tabGps[2].replace("xxxx",""));

        Toast.makeText(getApplicationContext(),String.valueOf(latitude),Toast.LENGTH_LONG).show();
        txtlatitude.setText(tabGps[0]);
        txtlongitude.setText(tabGps[1]);


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
