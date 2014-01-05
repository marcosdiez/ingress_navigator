package com.marcosdiez.ingressportalnavigator;

/**
 * Created by Marcos on 1/5/14.
 */
public class GoogleMapsUrl {
    String targetUrl="https://maps.google.com/maps";
    int counter=0;

    public GoogleMapsUrl(){
    }

    // https://maps.google.com/maps?saddr=-24.3,-47.3&daddr=-24.4,-47.4+to:-24.5,-47.5+to:-24.6,-47.6

    public void addTarget(Portal thePortal){
        addTarget(thePortal.lat, thePortal.lng);
    }

    public void addTarget(double lat, double lng){
        String latLng = + lat + "," + lng;

        switch(counter){
            case 0:
                targetUrl+="?saddr=" + latLng;
                break;
            case 1:
                targetUrl+= "&daddr=" + latLng;
                break;
            default:
                targetUrl+= "+to:" + latLng;
                break;
        }
        counter++;
    }

    public String getTargetUrl(){
        return targetUrl;
    }
}
