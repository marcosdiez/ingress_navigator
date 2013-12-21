package com.marcosdiez.ingressportalnavigator;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

    public double distanceFromHere(double toLat, double toLng){
        //get location
        location=locationManager.getLastKnownLocation(provider);
        if(location == null ){
            return 0;
        }
        lng=location.getLongitude();
        lat=location.getLatitude();
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

    public double lat;
    public double lng;

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
