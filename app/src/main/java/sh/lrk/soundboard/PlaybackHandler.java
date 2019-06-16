package sh.lrk.soundboard;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class PlaybackHandler {

    private static final String TAG = PlaybackHandler.class.getCanonicalName();

    private static PlaybackHandler instance;

    private final MediaPlayer player;

    private boolean playing = false;

    private PlaybackHandler() {
        player = new MediaPlayer();
    }

    public void play(File file) {
        if (!playing) {
            playing = true;
            try {
                player.setDataSource(file.getPath());
                startPlayback();
            } catch (IOException e) {
                Log.e(TAG, "Error setting data source!", e);
                player.reset();
                playing = false;
            }
        }
    }

    private void startPlayback() {
        try {
            player.setOnCompletionListener(mp -> {
                mp.reset();
                playing = false;
            });
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "Error playing media!", e);
            player.reset();
            playing = false;
        }
    }

    public static PlaybackHandler getInstance() {
        if (instance == null) {
            instance = new PlaybackHandler();
        }
        return instance;
    }
}
