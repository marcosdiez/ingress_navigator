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
    Location location;
    public double lat;
    public double lng;

    public String distanceFromHereStr(double toLat, double toLng){
        double distance = distanceFromHere(toLat,toLng);
        if(distance < 1000){
            return Math.round(distance) + "m";
        }
        if(distance < 10000){
            return ((float)Math.round(distance/100))/10.0 + "km";
        }
        return Math.round(distance/1000) + "km";
    }

    public Location GetNewLocation(){
        Location newLocation = locationManager.getLastKnownLocation(provider);
        if(newLocation!=null){
            location = newLocation;
        }
        if(location != null){
            lng=location.getLongitude();
            lat=location.getLatitude();
        }
        return location;
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
        GetNewLocation();
        return distanceFromHereHelper(toLat, toLng);
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
        lat = loc.getLatitude();
        lng = loc.getLongitude();

        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);



//        /*-------to get City-Name from coordinates -------- */
//        String cityName = null;
//
//        Geocoder gcd = new Geocoder(theContext, Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(loc.getLatitude(),
//                    loc.getLongitude(), 1);
//            if (addresses.size() > 0)
//                Log.d(TAG,addresses.get(0).getLocality());
//            cityName = addresses.get(0).getLocality();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                + cityName;

        //Log.d(TAG, s);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
//    }
}
