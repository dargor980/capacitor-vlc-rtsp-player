package com.dargor980.plugins.vlcrtspplayer;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.content.Context;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.videolan.libvlc.*;
import org.videolan.libvlc.util.*;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@CapacitorPlugin(name = "VlcRtspPlayer")
public class VlcRtspPlayerPlugin extends Plugin {
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurface;
    private Handler handler = new Handler(Looper.getMainLooper());




    @PluginMethod 
    public void play(PluginCall call) {
        String url = call.getString("url");

        if(url == null) {
            call.reject("URL required");
            return;
        }

        getActivity().runOnUiThread(() -> {
            Context context = getContext();

            if(libVLC == null) {
                ArrayList<String> args = new ArrayList<>();
                args.add("--no-drop-late-frames");
                args.add("--no-skip-frames");
                libVLC = new LibVLC(context, args);
            }

            if(mediaPlayer == null ) {
                mediaPlayer = new MediaPlayer(libVLC);
            }

            videoSurface = new SurfaceView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

            getActivity().addContentView(videoSurface, params);
            mediaPlayer.getVLCVout().setVideoView(videoSurface);
            mediaPlayer.getVLCVout().attachViews();

            Media media = new Media(libVLC, Uri.parse(url));
            media.setHWDecoderEnabled(true, false);
            media.addOption(":network-caching=150");
            mediaPlayer.setMedia(media);
            mediaPlayer.play();

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
}
