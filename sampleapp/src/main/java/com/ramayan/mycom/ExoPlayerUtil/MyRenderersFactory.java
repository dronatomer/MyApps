package com.ramayan.mycom.ExoPlayerUtil;

import android.content.Context;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;

import java.util.ArrayList;

public class MyRenderersFactory extends DefaultRenderersFactory
{

    public MyRenderersFactory(Context context)
    {
        super(context);
    }

    public MyRenderersFactory(Context context, DrmSessionManager drmsessionmanager)
    {
        super(context, drmsessionmanager);
    }

    public MyRenderersFactory(Context context, DrmSessionManager drmsessionmanager, int i)
    {
        super(context, drmsessionmanager, i);
    }

    public MyRenderersFactory(Context context, DrmSessionManager drmsessionmanager, int i, long l)
    {
        super(context, drmsessionmanager, i, l);
    }

    protected void buildAudioRenderers(Context context, DrmSessionManager drmsessionmanager, AudioProcessor aaudioprocessor[], Handler handler, AudioRendererEventListener audiorenderereventlistener, int i, ArrayList arraylist) {
        arraylist.add(new MyMediaCodecAudioRenderer(context, MediaCodecSelector.DEFAULT, drmsessionmanager, true, handler, audiorenderereventlistener, AudioCapabilities.getCapabilities(context), aaudioprocessor));


    }
}
