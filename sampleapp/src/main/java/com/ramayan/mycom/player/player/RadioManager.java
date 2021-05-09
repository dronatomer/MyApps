package com.ramayan.mycom.player.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.android.exoplayer2.Format;
import com.ramayan.mycom.Model.MediaMetaData;

import org.greenrobot.eventbus.EventBus;


public class RadioManager {

    private static RadioManager instance = null;
    private static RadioService service;
    private Context context;
    private boolean serviceBound;
     MediaMetaData currentitem;

    private RadioManager(Context context) {
        this.context = context;
        serviceBound = false;
    }

    public boolean isServiceBound() {
        return serviceBound;
    }

    public static RadioManager with(Context context) {

        // if (instance == null)
        instance = new RadioManager(context);

        return instance;
    }

    public static RadioService getService() {
        return service;
    }

    public void playOrPause(String streamUrl) {
        if(currentitem!=null) {
            service.sendMediaList(currentitem);
            service.playOrPause(streamUrl);
        }
    }

    public String getStatus(){
        if(service!=null)
            return service.getStatus();
        return "";
    }

    public void sendMediaList(MediaMetaData currentItem) {

        this.currentitem = currentItem;
        if (service != null)
            service.sendMediaList(currentItem);


    }


    public Format getFormat() {
        return service.getFormat();
    }

    public void getUpdateResources() {
       // ((HomeActivity) context).updateResources();
    }

    public boolean isPlaying() {
        return service.isPlaying();
    }

    public void bind() {

        Intent intent = new Intent(context, RadioService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (service != null)
            EventBus.getDefault().post(service.getStatus());
    }

    public void unbind() {
        serviceBound = false;
        context.unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {

            service = ((RadioService.LocalBinder) binder).getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    public void pause(){
        if(service!=null)
            service.pause();
    }

    public void resume(){
        if(service!=null)
            service.resume();
    }
}
