package com.marcosdiez.ingressportalnavigator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Marcos on 1/18/14.
 */
public class GoogleEarth {

    public static void openGoogleEarth(Activity theActivity){
        File outputFile = createKmlFile();
        if(outputFile==null){
            return;
        }
        Uri earthURI = Uri.fromFile(outputFile);
        Intent earthIntent = new Intent(Intent.ACTION_VIEW);
        earthIntent.setDataAndType(earthURI, "application/vnd.google-earth.kml+xml");
        try{
            theActivity.startActivity(earthIntent);
        }catch( android.content.ActivityNotFoundException e){
            Toast.makeText(Globals.getContext(),"Google Earth not installed ?",Toast.LENGTH_SHORT).show();
        }
    }

    public static void exportCheckedPortals(Activity theActivity){
        File outputFile = GoogleEarth.createKmlFile();
        if(outputFile == null){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.google-earth.kml+xml");
        intent.putExtra(Intent.EXTRA_SUBJECT, "List of Portals");
        intent.putExtra(Intent.EXTRA_TEXT,
                PortalList.getPortalList().makeTextOfLikedPortals());

        Uri earthURI = Uri.fromFile(outputFile);
        intent.putExtra(Intent.EXTRA_STREAM, earthURI);
        theActivity.startActivity(Intent.createChooser(intent, "Send email..."));
    }

    static File createKmlFile(){
        String output = PortalList.getPortalList().makeKmlOfLikedPortals();
        if(output == null){
            Toast.makeText(Globals.getContext(), "No portals were chosen", Toast.LENGTH_SHORT).show();
            return null;
        }
        File outputDir = new File(Globals.getPublicWritableFolder());
        try {
            File outputFile = File.createTempFile("exported_portals", ".kml", outputDir);
            outputFile.setReadable(true,false);
            FileWriter out = new FileWriter(outputFile);
            out.write(output);
            out.flush();
            out.close();
            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
