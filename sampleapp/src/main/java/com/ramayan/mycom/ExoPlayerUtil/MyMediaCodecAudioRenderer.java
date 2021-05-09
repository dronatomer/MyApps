// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.ramayan.mycom.ExoPlayerUtil;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;

import java.nio.ByteBuffer;

public class MyMediaCodecAudioRenderer extends MediaCodecAudioRenderer
{
    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        super(context, mediaCodecSelector);
    }

    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys);
    }

    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        super(context, mediaCodecSelector, eventHandler, eventListener);
    }

    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener);
    }

    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, @Nullable AudioCapabilities audioCapabilities, AudioProcessor... audioProcessors) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioCapabilities, audioProcessors);
    }

    public MyMediaCodecAudioRenderer(Context context, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable AudioRendererEventListener eventListener, AudioSink audioSink) {
        super(context, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioSink);
    }


    public ByteBuffer deepCopy(ByteBuffer source) {
        ByteBuffer target=null;
        int sourceP = source.position();
        int sourceL = source.limit();

        if (null == target) {
            target = ByteBuffer.allocate(source.remaining());
        }
        target.put(source);
        target.flip();

        source.position(sourceP);
        source.limit(sourceL);
        return target;
    }




//    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec mediacodec, ByteBuffer bytebuffer, int bufferIndex,
//            int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip)
//        throws ExoPlaybackException
//    {
//        boolean flag1 = RecordManager.getInstance().getRecordState();
//        ByteBuffer bytebuffer1 = null;
//        Integer integer = -1;
//        if (flag1)
//        {
//            integer = bytebuffer.position();
//            bytebuffer1 = deepCopy(bytebuffer);
//        }
//        shouldSkip = super.processOutputBuffer(positionUs, elapsedRealtimeUs, mediacodec, bytebuffer, bufferIndex, bufferFlags, bufferPresentationTimeUs, shouldSkip);
//        if (flag1)
//        {
//
//            bytebuffer1.limit(bytebuffer.position() - integer);
//            RecordManager.getInstance().appendBuffer(bytebuffer1);
//       }
//        return shouldSkip;
//    }
}
