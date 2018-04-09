package m2gi.dz.satelliteapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import m2gi.dz.satelliteapp.R;
import m2gi.dz.satelliteapp.adapter.RecyclerItemClickListener;
import m2gi.dz.satelliteapp.adapter.SatelitteAdapter;
import m2gi.dz.satelliteapp.model.Satellite;

public class ListSatelliteActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    ArrayList<Satellite> satellites ;
    String angle,latitude,longitude,altitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_satellite);

        Intent intent = getIntent();

        angle = intent.getStringExtra("ongle");
        latitude  = intent.getStringExtra("latitude");
        longitude  = intent.getStringExtra("longitude");
        altitude  = intent.getStringExtra("altitude");

        latitude = "45.193608";
        longitude = "5.764625";
        altitude = "200";

        satellites = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SatelitteAdapter adapter = new SatelitteAdapter(satellites);
        recyclerView.setAdapter(adapter);
       // http://www.n2yo.com/rest/v1/satellite/above/"+latitude+"/"+longitude+"/"+altitude+"/"+angle+"/18&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,"http://www.n2yo.com/rest/v1/satellite/above/"+latitude+"/"+longitude+"/"+altitude+"/"+angle+"/18&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                        // the response is already constructed as a JSONObject!
                        try {

                            JSONArray jsonArray = response.getJSONArray("above");


                            for(int i = 0 ; i<jsonArray.length();i++){

                                Satellite satellite = new Satellite();

                                JSONObject object = jsonArray.getJSONObject(i);

                                satellite.setId(object.getString("satid"));
                                satellite.setName(object.optString("satname"));

                                satellites.add(satellite);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SatelitteAdapter adapter = new SatelitteAdapter(satellites);
                        recyclerView.setAdapter(adapter);
                        recyclerView.addOnItemTouchListener(
                                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {

                                        Intent intent = new Intent(ListSatelliteActivity.this, DetailSatelliteActivity.class);

                                        intent.putExtra("id",satellites.get(position).getId());
                                        intent.putExtra("name",satellites.get(position).getName());

                                        startActivity(intent);

                                    }
                                })
                        );
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
