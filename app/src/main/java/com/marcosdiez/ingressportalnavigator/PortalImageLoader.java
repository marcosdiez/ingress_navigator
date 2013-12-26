package com.marcosdiez.ingressportalnavigator;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by Marcos on 12/25/13.
 */
public class PortalImageLoader {
    ImageView image_portal;
    ProgressBar loading_spinner;
    public Portal thePortal;
    private static String TAG = "PortalImageLoader";

    public PortalImageLoader(ImageView image_portal, ProgressBar loading_spinner, Portal thePortal){
        this.image_portal = image_portal;
        this.loading_spinner = loading_spinner;
        this.thePortal = thePortal;
    }

    void loadImage(){
        String theImage = thePortal.getExpectedImageFile();
        File theImageFile = new File(theImage);
        if(theImageFile.exists() && theImageFile.length() > ( 1024 * 1024 )){
            if(theImage != null){
                Drawable theImageDrawable = Drawable.createFromPath(theImage);
                image_portal.setImageDrawable(theImageDrawable);
            }
        }else{
            Log.d(TAG,"Initializing Background Downloader for "+ this.thePortal.imageUrl);
            // do it in background
            loading_spinner.setVisibility(View.VISIBLE);
            new PortalImageLoaderHelper().execute(this);
        }
    }
}
