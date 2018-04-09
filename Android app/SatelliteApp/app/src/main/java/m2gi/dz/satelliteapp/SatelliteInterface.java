package m2gi.dz.satelliteapp;

import java.util.ArrayList;
import java.util.List;

import m2gi.dz.satelliteapp.model.Satellite;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by chemali on 24/03/2018.
 */

public interface SatelliteInterface {

    @GET("/rest/v1/satellite/positions/22077/45/5/200/1&apiKey=X89TH9-A5E6R3-BKEVGH-3R4W")
    Call<List<Satellite>> getSat();




}
