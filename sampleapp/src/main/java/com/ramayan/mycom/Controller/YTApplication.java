package com.ramayan.mycom.Controller;

import android.app.Application;
import android.content.Context;

import com.facebook.ads.AudienceNetworkAds;
import com.ramayan.mycom.player.player.RadioManager;


/**
 * Created by smedic on 5.3.17..
 */

public class YTApplication extends Application {

    private static Context mContext;
    private static RadioManager radioManager;

    public void onCreate() {
        super.onCreate();
        AudienceNetworkAds.initialize(this);
        radioManager = RadioManager.with(this);
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
    public static RadioManager getRadioManager(){
        return radioManager;
    }


}
