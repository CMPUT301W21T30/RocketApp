package com.example.rocketapp.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Helper class for dealing with device storage
 */
public class Device {

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Saves an image to the phones photo gallery
     * @param drawable drawable to save to phone photo gallery
     * @param activity activity called from
     */
    public static void saveToGallery(Drawable drawable, Activity activity){
        /*
         * https://www.youtube.com/watch?v=FcCtT1C7NGI
         * Author: Ketul Patel
         *
         * https://stackoverflow.com/questions/26718374/save-image-from-imageview-to-device-gallery
         * Author: Shravan DG  https://stackoverflow.com/users/6646750/shravan-dg
         */

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/QRcodes");

        dir.mkdirs();
        String filename = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream);
        try {
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        activity.sendBroadcast(intent);
    }
}
