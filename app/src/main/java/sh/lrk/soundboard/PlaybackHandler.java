package sh.lrk.soundboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

import static sh.lrk.soundboard.settings.SettingsActivity.DEFAULT_PLAY_IMMEDIATELY;
import static sh.lrk.soundboard.settings.SettingsActivity.DEFAULT_STOP_PLAY;
import static sh.lrk.soundboard.settings.SettingsActivity.KEY_PLAY_IMMEDIATELY;
import static sh.lrk.soundboard.settings.SettingsActivity.KEY_STOP_PLAY;

class PlaybackHandler {

    private static final String TAG = PlaybackHandler.class.getCanonicalName();

    private static PlaybackHandler instance;

    private final MediaPlayer player;

    private boolean playing = false;
    private ImageButton currentlyPlaying = null;

    private PlaybackHandler() {
        player = new MediaPlayer();
    }

    void play(File file, ImageButton playBtn, Context context) {
        if (!playing) {
            preparePlayback(file, playBtn, context);
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(KEY_STOP_PLAY, DEFAULT_STOP_PLAY)) {
                stopPlayback(context);

                if (preferences.getBoolean(KEY_PLAY_IMMEDIATELY, DEFAULT_PLAY_IMMEDIATELY)) {
                    preparePlayback(file, playBtn, context);
                }
            }
        }
    }

    private void preparePlayback(File file, ImageButton playBtn, Context context) {
        playing = true;
        currentlyPlaying = playBtn;
        currentlyPlaying.setImageDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_red_900_48dp));
        try {
            player.setDataSource(file.getPath());
            startPlayback(playBtn, context);
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source!", e);
            player.reset();
            currentlyPlaying.setImageDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
            playing = false;
        }
    }

    private void stopPlayback(Context context) {
        player.stop();
        player.reset();
        playing = false;
        if (currentlyPlaying != null) {
            currentlyPlaying.setImageDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
            currentlyPlaying = null;
        }
    }

    private void startPlayback(ImageButton playBtn, Context context) {
        try {
            player.setOnCompletionListener(mp -> {
                mp.reset();
                playing = false;
                currentlyPlaying.setImageDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
            });

            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "Error playing media!", e);
            player.reset();
            currentlyPlaying.setImageDrawable(context.getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
            playing = false;
        }
    }

    static PlaybackHandler getInstance() {
        if (instance == null) {
            instance = new PlaybackHandler();
        }
        return instance;
    }
}
