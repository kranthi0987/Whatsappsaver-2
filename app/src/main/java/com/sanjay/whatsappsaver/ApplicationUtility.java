package com.sanjay.whatsappsaver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by User on 1/6/2018.
 */

public class ApplicationUtility {

        ConnectivityManager connectivityManager;
        NetworkInfo info;

    public boolean checkConnection(Context context) {
        boolean flag = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                System.out.println(info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                System.out.println(info.getTypeName());
                flag = true;
            }
        } catch (Exception exception) {
            System.out.println("Exception at network connection....."
                    + exception);
        }
        return flag;
    }


}
