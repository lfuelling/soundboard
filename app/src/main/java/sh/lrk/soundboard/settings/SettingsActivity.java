package sh.lrk.soundboard.settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import sh.lrk.soundboard.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String TAG = SettingsActivity.class.getCanonicalName();

    public static final String KEY_TEXT_SIZE = "text_size";
    public static final String KEY_STOP_PLAY = "stop_on_play_other";
    public static final String KEY_PLAY_IMMEDIATELY = "play_immediately";
    public static final String KEY_NUM_COLS = "num_cols";

    public static final String DEFAULT_TEXT_SIZE = "2";
    public static final boolean DEFAULT_STOP_PLAY = true;
    public static final boolean DEFAULT_PLAY_IMMEDIATELY = true;
    public static final String DEFAULT_NUM_COLS = "4";
    public static final int MIN_NUM_COLS = 1;
    public static final int MAX_NUM_COLS = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AdvancedPreferenceFragment.class.getName().equals(fragmentName)
                || CustomizationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CustomizationPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customization);
            setHasOptionsMenu(true);

            Preference numColsPreference = findPreference(KEY_NUM_COLS);
            numColsPreference.setOnPreferenceClickListener(p -> {
                showNumColsDialog();
                return true;
            });
        }

        @SuppressLint("CutPasteId")
        private void showNumColsDialog() {
            AlertDialog dia = new AlertDialog.Builder(getActivity())
                    .setView(R.layout.num_col_prompt)
                    .setNegativeButton(R.string.cancel, (dialog, which) -> Log.d(TAG, "Change canceled."))
                    .create();

            dia.setOnShowListener(dialog -> {
                String numColsString = getPreferenceManager().getSharedPreferences().getString(KEY_NUM_COLS, DEFAULT_NUM_COLS);
                if (numColsString == null) {
                    numColsString = DEFAULT_NUM_COLS;
                }
                SeekBar seekBar = dia.findViewById(R.id.numColsSeekBar);
                TextView valueTextView = dia.findViewById(R.id.numColsValueTextView);

                seekBar.setProgress(Integer.parseInt(numColsString));
                valueTextView.setText(numColsString);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        valueTextView.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

            });

            dia.setButton(AlertDialog.BUTTON_NEUTRAL, getActivity().getText(R.string.reset), (d, w) -> {
                SeekBar numColsSeekBar = dia.findViewById(R.id.numColsSeekBar);
                numColsSeekBar.setProgress(Integer.parseInt(DEFAULT_NUM_COLS));
                getPreferenceManager().getSharedPreferences().edit()
                        .putString(KEY_NUM_COLS, DEFAULT_NUM_COLS)
                        .apply();
            });

            dia.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getText(R.string.okay), (d, w) -> {
                SeekBar numColsSeekBar = dia.findViewById(R.id.numColsSeekBar);
                int value = numColsSeekBar.getProgress();

                if (value >= MIN_NUM_COLS && value <= MAX_NUM_COLS) {
                    getPreferenceManager().getSharedPreferences().edit()
                            .putString(KEY_NUM_COLS, String.valueOf(value))
                            .apply();
                } else if (value < MIN_NUM_COLS) {
                    getPreferenceManager().getSharedPreferences().edit()
                            .putString(KEY_NUM_COLS, String.valueOf(MIN_NUM_COLS))
                            .apply();
                }
            });

            dia.show();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AdvancedPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_advanced);
            setHasOptionsMenu(true);

            SwitchPreference stopPlayback = (SwitchPreference) findPreference(KEY_STOP_PLAY);
            SwitchPreference playImmediately = (SwitchPreference) findPreference(KEY_PLAY_IMMEDIATELY);

            playImmediately.setEnabled(stopPlayback.isChecked());

            stopPlayback.setOnPreferenceChangeListener((p, n) -> {
                playImmediately.setEnabled((Boolean) n);
                return true;
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}