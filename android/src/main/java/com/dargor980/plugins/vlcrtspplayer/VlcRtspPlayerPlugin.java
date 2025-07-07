package com.dargor980.plugins.vlcrtspplayer;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.content.Context;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.provider.Settings;
import android.content.Intent;

import org.videolan.libvlc.*;
import org.videolan.libvlc.util.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@CapacitorPlugin(name = "VlcRtspPlayer")
public class VlcRtspPlayerPlugin extends Plugin {
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurface;
    private Handler handler = new Handler(Looper.getMainLooper());

    private RtspOverlayView overlayView;
    private WindowManager windowManager;



    @PluginMethod 
    public void checkOverlayPermission(PluginCall call) {
        Context context = getContext();
        boolean granted = Settings.canDrawOverlays(context);
        JSObject result = new JSObject();
        result.put("granted", granted);
        call.resolve(result);
    }

    @PluginMethod 
    public void requestOverlayPermission(PluginCall call) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getContext().getPackageName()));
            getActivity().startActivity(intent);
            call.resolve();
    }

    @PluginMethod 
    public void play(PluginCall call) {
        String url = call.getString("url");

        int x = call.getInt("x", 0);
        int y = call.getInt("y", 0);
        int width = call.getInt("width", 720);
        int height = call.getInt("height", 480);

        if(url == null || url.isEmpty()) {
            call.reject("URL required");
            return;
        }

        bridge.getActivity().runOnUiThread(() -> {
            Context context = getContext();
            overlayView = new RtspOverlayView(context, url);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            );
            params.x = x;
            params.y = y;

            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(overlayView, params);

            call.resolve();
        });

      
     
    }

    @PluginMethod 
    public void pause(PluginCall call) {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            call.resolve();
        } else {
            call.reject("No stream is currently playing");
        }
    }

    @PluginMethod 
    public void updateStream(PluginCall call) {
        String url = call.getString("url");

        if(url == null) {
            call.reject("URL required");
            return;
        }

        getActivity().runOnUiThread(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            Media media = new Media(libVLC, Uri.parse(url));
            media.setHWDecoderEnabled(true, false);
            media.addOption(":network-caching=150");
            mediaPlayer.setMedia(media);
            mediaPlayer.play();

            call.resolve();
        });
    }

    @PluginMethod
    public void checkConnection(PluginCall call) {
        String url = call.getString("url");

        if(url == null || url.isEmpty()) {
            call.reject("Invalid URL");
            return;
        }

        if (libVLC == null) {
            ArrayList<String> options = new ArrayList<>();
            options.add("--network-caching=1000");
            libVLC = new LibVLC(getContext(), options);
        }


        Media media = new Media(libVLC, Uri.parse(url));
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=1000");

        MediaPlayer mediaPlayer = new MediaPlayer(libVLC);

        mediaPlayer.setMedia(media);

        AtomicBoolean responded = new AtomicBoolean(false);

        mediaPlayer.setEventListener(event -> {
            switch (event.type) {
                case MediaPlayer.Event.Opening:
                    break;
                case MediaPlayer.Event.Playing:
                    handler.post(() -> {
                        JSObject ret = new JSObject();
                        ret.put("connected", true);
                        call.resolve(ret);
                        mediaPlayer.release();
                        media.release();
                    });
                    break;
                case MediaPlayer.Event.EncounteredError:
                case MediaPlayer.Event.EndReached:
                    handler.post(() -> {
                        JSObject ret = new JSObject();
                        ret.put("connected", false);
                        call.resolve(ret);
                        mediaPlayer.release();
                        media.release();
                    });
                    break;
            }
        });

        mediaPlayer.play();

        handler.postDelayed(() -> {
            if (responded.getAndSet(true)) return;
            JSObject ret = new JSObject();
            ret.put("connected", false);
            call.resolve(ret);
            mediaPlayer.release();
            media.release();
        }, 5000);
    }

    @PluginMethod
    public void hide(PluginCall call) {
        bridge.getActivity().runOnUiThread(() -> {
            if(overlayView != null && windowManager != null) {
                overlayView.release();
                windowManager.removeView(overlayView);
                overlayView = null;
                windowManager = null;
            }
            call.resolve();
        });
    }

    private static class RtspOverlayView extends FrameLayout {
        private TextureView textureView;
        private MediaPlayer mediaPlayer;
        private LibVLC libVLC;

        public RtspOverlayView(Context context, String url) {
            super(context);
            initPlayer(context, url);
        }

        public void initPlayer(Context context, String url) {
            textureView = new TextureView(context);
            this.addView(textureView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ));

            ArrayList<String> options = new ArrayList<>();
            options.add("--network-caching=150");

            libVLC = new LibVLC(context, options);
            mediaPlayer = new MediaPlayer(libVLC);
            mediaPlayer.getVLCVout().setVideoView(textureView);
            mediaPlayer.getVLCVout().attachViews();

            Media media = new Media(libVLC, Uri.parse(url));
            media.setHWDecoderEnabled(true, false);
            mediaPlayer.setMedia(media);
            mediaPlayer.play();
        }

        public void release() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.getVLCVout().detachViews();
                mediaPlayer.release();
                libVLC.release();
            }
        }
    }
}

