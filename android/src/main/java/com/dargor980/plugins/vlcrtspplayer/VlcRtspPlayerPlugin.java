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

import java.util.ArrayList;

@CapacitorPlugin(name = "VlcRtspPlayer")
public class VlcRtspPlayerPlugin extends Plugin {
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurface;




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
}
