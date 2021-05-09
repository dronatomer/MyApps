package com.ramayan.mycom.ExoPlayerUtil;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import androidx.media.session.MediaButtonReceiver;

import com.ramayan.mycom.Controller.YTApplication;
import com.ramayan.mycom.player.player.RadioManager;


public class MyMediaButtonReceiver extends MediaButtonReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String intentAction = intent.getAction();
            if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                return;
            }
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }

            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {
                RadioManager radioManager = YTApplication.getRadioManager();
                if (radioManager.isServiceBound() && radioManager.isPlaying())
                    radioManager.pause();
                else radioManager.resume();

            }


            abortBroadcast();
        } catch (Exception e) {
            e.getMessage();
        }
    }
}