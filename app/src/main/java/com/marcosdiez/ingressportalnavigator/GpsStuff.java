package com.marcosdiez.ingressportalnavigator;


import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Marcos on 12/20/13.
 */
public class GpsStuff implements LocationListener {
    private static final String TAG = "ING_GpsStuff";
    private static GpsStuff myGpsStuff=null;

    LocationManager locationManager;
    Criteria c = new Criteria();
    String provider;

    public double lat=0;
    public double lng=0;
    boolean useGps = true;

    public String distanceFromHereStr(double toLat, double toLng){
        double distance = distanceFromHere(toLat,toLng);
        return distanceAsPrettyString(distance);
    }

    public static String distanceAsPrettyString(double distance) {
        if(distance < 1000){
            return Math.round(distance) + "m";
        }
        if(distance < 10000){
            return ((float)Math.round(distance/100))/10.0 + "km";
        }
        return Math.round(distance/(1000*1000)) + "km";
    }

    public void setLocationToGps(){
        useGps=true;
    }

    public void setLocationManual(double lat, double lng){
        useGps=false;
        this.lat=lat;
        this.lng=lng;
    }

    public void refreshLocation(){
        if(useGps){
            Location newLocation = locationManager.getLastKnownLocation(provider);
            if(newLocation!=null){
                Location location = newLocation;
                if(location != null){
                    lng=location.getLongitude();
                    lat=location.getLatitude();
                }
            }
        }
    }

    public String locationToAddress(double lat, double lng){
        Geocoder geocoder = new Geocoder(Globals.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if(addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strReturnedAddress.append(returnedAddress.getCountryName()).append("\n");
                return strReturnedAddress.toString();
            }
            else{
                return "";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  null;
        }
    }


    public double distanceFromHere(double toLat, double toLng){
        refreshLocation();
        return distanceFromHereHelper(toLat, toLng);
    }


    public static double distanceBetween(double lat1, double lng1, double lat2, double lng2){
        float result[] = { 0 };
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        return result[0];
    }


    double distanceFromHereHelper(double toLat, double toLng){
        float result[] = { 0 };
        Location.distanceBetween(lat, lng, toLat, toLng, result);
        Log.d(TAG, "lat:" + lat + " " + " lng " + lng + " toLat " + toLat + " toLng " + toLng + " result "  + result[0]);
        return result[0];
    }

    public static synchronized GpsStuff getMyGpsStuff(){
        if(myGpsStuff==null){
            myGpsStuff = new GpsStuff();
        }
        return myGpsStuff;
    }

    private GpsStuff(){
        locationManager = (LocationManager) Globals.getContext().getSystemService(Globals.getContext().LOCATION_SERVICE);
        provider=locationManager.getBestProvider(c, false);
    }



    @Override
    public void onLocationChanged(Location loc) {
        if(useGps){
            lat = loc.getLatitude();
            lng = loc.getLongitude();

            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
