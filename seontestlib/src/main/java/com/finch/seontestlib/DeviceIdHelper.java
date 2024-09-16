package com.finch.seontestlib;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeviceIdHelper {

    private String strDeviceId = null;
    private Thread thread = null;

    public DeviceIdHelper(Context context)
    {
        thread = new Thread(() -> {
            strDeviceId = calculateDeviceId(context);
        });
        thread.start();
    }

    public  String getDeviceId() throws DeviceIdError {
        if (strDeviceId == null)
        {
            throw new DeviceIdError();
        }
        return strDeviceId;
    }

    private String calculateDeviceId(Context context)
    {
        String deviceId = "";
        String manufacture = Build.MANUFACTURER;         // Manufacture
        String model = Build.MODEL;                      // Model

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;    // Width pixels
        int height = metrics.heightPixels; // Height pixels
        float density = metrics.density;  // Display density (dpi)

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID); // Unique id


        deviceId = manufacture + model + width + height + density + androidId;
        deviceId = getSHA512(deviceId);
        return deviceId;
    }

    private String getSHA512(String input) {
        String hashText = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            hashText = no.toString(16);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashText;
    }
}

