package sh.lrk.soundboard.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

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

            stopPlayback.setOnPreferenceChangeListener((p,n) -> {
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