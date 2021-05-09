package com.ramayan.mycom.player.player;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ramayan.mycom.ExoPlayerUtil.MyMediaButtonReceiver;
import com.ramayan.mycom.ExoPlayerUtil.MyRenderersFactory;
import com.ramayan.mycom.Model.MediaMetaData;
import com.ramayan.mycom.R;
import com.ramayan.mycom.Utils.AppConstants;

import org.greenrobot.eventbus.EventBus;

public class RadioService extends Service implements Player.EventListener, AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PLAY = "com.mcakir.radio.player.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.mcakir.radio.player.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.mcakir.radio.player.ACTION_STOP";
    public static final String ACTION_NEXT = "com.mcakir.radio.player.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.mcakir.radio.player.ACTION_PREVIOUS";

    private final IBinder iBinder = new LocalBinder();

    private Handler handler;
    private final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;
    private WifiManager.WifiLock wifiLock;
    private AudioManager audioManager;
    private MediaNotificationManager notificationManager;
    private String status;
    private String strAppName;
    private String strLiveBroadcast;
    private String streamUrl;
  //  private ArrayList<MediaMetaData> playList;
    private MediaMetaData currentMediaObject = null;
    private boolean resumeOnFocusGain = false;

    public class LocalBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }
    };

    private void notifyNextPrev() {
        Intent intent = new Intent("notify");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_OFFHOOK
                    || state == TelephonyManager.CALL_STATE_RINGING) {
                if (!isPlaying()) return;
                onGoingCall = true;
                stop();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return;
                onGoingCall = false;
                resume();
            }
        }
    };

    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();
            pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            stop();
            notificationManager.cancelNotify();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            resume();
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
                if (event.getAction() == KeyEvent.ACTION_UP && !event.isLongPress()) {
                    if (isPlaying()) {
                        pause();
                    } else {
                        resume();
                    }
                }
                return true;
            } else {
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        strAppName = getResources().getString(R.string.app_name);
        if (currentMediaObject != null)
            strLiveBroadcast = currentMediaObject.getMediaTitle();
        onGoingCall = false;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.registerMediaButtonEventReceiver(new ComponentName(
                this,
                MyMediaButtonReceiver.class));
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        notificationManager = new MediaNotificationManager(this);


        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");

        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null, getApplicationContext(), MyMediaButtonReceiver.class);
        mediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0));

        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
                .build());
        mediaSession.setCallback(mediasSessionCallback);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        handler = new Handler();
        DefaultLoadControl loadControl = new DefaultLoadControl();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        MyRenderersFactory myRenderersFactory = new MyRenderersFactory(this);
        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(myRenderersFactory, trackSelector, loadControl);
        exoPlayer.addListener(this);
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        status = PlaybackStatus.IDLE;
    }

    public Format getFormat() {
        return exoPlayer.getAudioFormat();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
        String action = intent.getAction();
        if (action != null) {
                if (TextUtils.isEmpty(action))
                    return START_NOT_STICKY;
                int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    stop();
                    return START_NOT_STICKY;
                }
                if (action.equalsIgnoreCase(ACTION_PLAY)) {
                    transportControls.play();
                } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
                    transportControls.pause();
                } else if (action.equalsIgnoreCase(ACTION_STOP)) {
                    transportControls.stop();
//                } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
//                    if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0) < playList.size() - 1) {
//                        SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.POSITION, SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0) + 1);
//                        //AppConstants.position = AppConstants.position + 1;
//                        notifyNextPrev();
//                        currentMediaObject = playList.get(SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0));
//                        playOrPause(playList.get(SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0)).getMediaUrl());
//                    } else
//                        Toast.makeText(this, getString(R.string.no_more_stations), Toast.LENGTH_SHORT).show();
//
//                } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
//                    if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0) > 0) {
//                        SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.POSITION, SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0) - 1);
//                        //AppConstants.position = AppConstants.position - 1;
//                        notifyNextPrev();
//                        currentMediaObject = playList.get(SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0));
//                        playOrPause(playList.get(SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.POSITION, 0)).getMediaUrl());

                    } else
                        Toast.makeText(this, getString(R.string.no_more_stations), Toast.LENGTH_SHORT).show();
//                }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(final Intent intent) {

    }


    @Override
    public void onDestroy() {
        pause();
        exoPlayer.release();
        exoPlayer.removeListener(this);
        stopSelf();
        if (telephonyManager != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        notificationManager.cancelNotify();
        mediaSession.release();
        unregisterReceiver(becomingNoisyReceiver);
        super.onDestroy();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (resumeOnFocusGain) {
                    resume();
                }
                exoPlayer.setVolume(0.8f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                if (isPlaying())
                    pause();

                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                boolean resume = isPlaying() || resumeOnFocusGain;

                if (isPlaying()) pause();

                resumeOnFocusGain = resume;


                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                if (isPlaying())
                    exoPlayer.setVolume(0.1f);

                break;
        }

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = PlaybackStatus.STOPPED;
                break;
            case Player.STATE_IDLE:
                status = PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
                SharedPreferences.Editor editor = getSharedPreferences(AppConstants.PREFERENCE_NAME, MODE_PRIVATE).edit();
                editor.putInt(AppConstants.RADIO_BITRATE, exoPlayer.getAudioFormat().sampleRate);
                editor.apply();
                break;
            default:
                status = PlaybackStatus.IDLE;
                break;
        }

        if (!status.equals(PlaybackStatus.IDLE))
            notificationManager.startNotify(status, currentMediaObject);

        EventBus.getDefault().post(status);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        EventBus.getDefault().post(PlaybackStatus.ERROR);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public void play(String streamUrl) {

        try {
            this.streamUrl = streamUrl;
            if (wifiLock != null && !wifiLock.isHeld()) {
                wifiLock.acquire();
            }

//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent());
            if (streamUrl.contains("m3u8")) {
                String userAgent = Util.getUserAgent(this, "User Agent");
                DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                        userAgent, null,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        1800000,
                        true);
                HlsMediaSource mediaSource = new HlsMediaSource(Uri.parse(streamUrl), dataSourceFactory, 1800000,
                        handler, null);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            } else {
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, getUserAgent(), BANDWIDTH_METER);

                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .setExtractorsFactory(new DefaultExtractorsFactory())
                        .createMediaSource(Uri.parse(streamUrl));

                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (streamUrl != null)
            play(streamUrl);
        resumeOnFocusGain = false;
    }

    public void pause() {
        exoPlayer.setPlayWhenReady(false);
        audioManager.abandonAudioFocus(this);
        resumeOnFocusGain = false;
        wifiLockRelease();
    }

    public void stop() {
        exoPlayer.stop();
        audioManager.abandonAudioFocus(this);
        resumeOnFocusGain = false;
        wifiLockRelease();
    }

    public void sendMediaList(MediaMetaData currentMediaObject) {
       // this.playList = playList;
        this.currentMediaObject = currentMediaObject;
    }

    public void playOrPause(String url) {
        if (streamUrl != null && streamUrl.equals(url)) {
            if (!isPlaying()) {
                play(streamUrl);
            } else {
                pause();
            }
        } else {
            if (isPlaying()) {
                pause();
            }
            play(url);
        }
    }

    public String getStatus() {
        return status;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public boolean isPlaying() {
        return this.status.equals(PlaybackStatus.PLAYING);
    }

    private void wifiLockRelease() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    private String getUserAgent() {
        return Util.getUserAgent(this, getClass().getSimpleName());
    }
}
