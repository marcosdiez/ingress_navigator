package com.marcosdiez.ingressportalnavigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Marcos on 1/18/14.
 */
public class GoogleEarth {

    public static void showExportDialog(Activity theActivity){
        final Activity activityCopy = theActivity;
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(theActivity);
        LayoutInflater inflater = theActivity.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_export_portals, null);
        alertDialog.setView(rootView);
        alertDialog.create().show();

        final CheckBox checkbox_export_checked_portals = (CheckBox) rootView.findViewById(R.id.checkbox_export_checked_portals);
        final CheckBox checkbox_export_portals_that_are_close = (CheckBox) rootView.findViewById(R.id.checkbox_export_portals_that_are_close);

        final TextView txt_portal_distance = (TextView) rootView.findViewById(R.id.edittext_portal_range);

        final Button button_share_portals = (Button) rootView.findViewById(R.id.button_share_portals);
        final Button button_open_google_earth = (Button) rootView.findViewById(R.id.button_open_google_earth);


        button_open_google_earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int maximumDistance = getMaximumDistance(checkbox_export_portals_that_are_close, txt_portal_distance);
                openGoogleEarth(activityCopy, checkbox_export_checked_portals.isChecked(), maximumDistance);
            }
        });
        button_share_portals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int maximumDistance = getMaximumDistance(checkbox_export_portals_that_are_close, txt_portal_distance);
                exportCheckedPortals(activityCopy, checkbox_export_checked_portals.isChecked(), maximumDistance);
            }
        });
    }

    private static int getMaximumDistance(CheckBox checkbox_export_portals_that_are_close, TextView txt_portal_distance) {
        int maximumDistance = 0;
        if(checkbox_export_portals_that_are_close.isChecked() ){
            maximumDistance = Integer.parseInt(txt_portal_distance.getText().toString()) * 1000; // meters
        }
        return maximumDistance;
    }

    private static void openGoogleEarth(Activity theActivity, boolean checkedPortals, double maximumDistance ){
        File outputFile = GoogleEarth.createKmlFile(checkedPortals, maximumDistance);
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
    public static void exportCheckedPortals(Activity theActivity, boolean checkedPortals, double maximumDistance){
        File outputFile = GoogleEarth.createKmlFile(checkedPortals, maximumDistance);
        if(outputFile == null){
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.google-earth.kml+xml");
        intent.putExtra(Intent.EXTRA_SUBJECT, "List of Portals");
        intent.putExtra(Intent.EXTRA_TEXT,
                PortalList.getPortalList().makeTextOfLikedPortals(checkedPortals , maximumDistance));

        Uri earthURI = Uri.fromFile(outputFile);
        intent.putExtra(Intent.EXTRA_STREAM, earthURI);
        theActivity.startActivity(Intent.createChooser(intent, "Send email..."));
    }

//    static File createKmlFile(){
//        return createKmlFile(true,0);
//    }

    static File createKmlFile(boolean checkedPortals, double maximumDistance /* meters */){
        String output = PortalList.getPortalList().makeKmlOfLikedPortals(checkedPortals , maximumDistance);
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
