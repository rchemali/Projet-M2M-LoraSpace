package m2gi.dz.satelliteapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chemali on 24/03/2018.
 */

public class Satellite {

    @SerializedName("satid")
    String id ;
    @SerializedName("satname")
    String name;
    @SerializedName("satlatitude")
    String latitude;
    @SerializedName("satlongitude")
    String longitude ;
    @SerializedName("satlaltitude")
    String altitude ;
    @SerializedName("azimuth")
    String azimuth;
    @SerializedName("elevation")
    String elevation;

    public Satellite(){}

    public Satellite(String id) {
        this.id = id;
    }

    public Satellite(String id, String name, String latitude, String longitude, String altitude, String azimuth, String elevation) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.azimuth = azimuth;
        this.elevation = elevation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }
}
