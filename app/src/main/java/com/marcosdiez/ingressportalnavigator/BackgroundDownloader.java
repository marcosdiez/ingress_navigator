package com.marcosdiez.ingressportalnavigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marcos on 12/24/13.
 */
public class BackgroundDownloader extends AsyncTask<PortalImageLoader, Void, Void> {
    PortalImageLoader theImageLoader=null;
    String theImage;
    private static String TAG = "BackgroundDownloader";

    protected Void doInBackground(PortalImageLoader... theImageLoader) {
        Log.d(TAG, "doInBackground");
        this.theImageLoader=theImageLoader[0];
        Portal thePortal = this.theImageLoader.thePortal;
        theImage = thePortal.getExpectedImageFile();
        DownloadImage(thePortal.imageUrl, thePortal.getExpectedImageFolder(), theImage);
        return null;
    }

    @Override
    protected void onPostExecute(Void whatever ) {
        Log.d(TAG, "onPostExecute");
        theImageLoader.loading_spinner.setVisibility(View.GONE);
        Drawable theImageDrawable = Drawable.createFromPath(theImage);
        theImageLoader.image_portal.setImageDrawable(theImageDrawable);
    }

    void DownloadImage(String theURL, String expectedDir, String destinationFile){
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(theURL);

            //create a new file, specifying the path, and the filename
            //which we want to save the file as.
            (new File(expectedDir)).mkdirs();
            File file = new File(destinationFile);

            //this will be used to write the downloaded data into the file we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            // Execute the request
            HttpResponse response;

            response = httpclient.execute(httpget);
            // Examine the response status
            Log.d("DownloadImage", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {
                // A Simple JSON Response Read
                InputStream inputStream = entity.getContent();

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    //downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);

                }
                //close the output stream when done
                inputStream.close();
                fileOutput.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}