package sh.lrk.soundboard;

import android.app.AlertDialog;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import static sh.lrk.soundboard.MainActivity.HIT;
import static sh.lrk.soundboard.MainActivity.IT_JUST_WORKS;
import static sh.lrk.soundboard.settings.SettingsActivity.DEFAULT_TEXT_SIZE;
import static sh.lrk.soundboard.settings.SettingsActivity.KEY_TEXT_SIZE;

class SampleAdapter extends ArrayAdapter<SoundboardSample> {

    private static final int ITEM_VIEW = R.layout.sample_list_item;
    private static final int ID_DELETE = 0;
    private static final int ID_EDIT = 1;
    private static final String TAG = SampleAdapter.class.getCanonicalName();

    SampleAdapter(MainActivity context) {
        super(context, ITEM_VIEW);
    }

    @androidx.annotation.NonNull
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.sample_list_item, parent, false);
        } else {
            view = convertView;
        }

        final SoundboardSample item = getItem(position);
        if (item != null) {

            TextView sampleNameTextView = view.findViewById(R.id.sampleName);
            ImageButton playBtn = view.findViewById(R.id.playBtn);

            setTextSize(sampleNameTextView);
            String sampleName = item.getName();
            sampleNameTextView.setText(sampleName);

            playBtn.setOnClickListener(v -> PlaybackHandler.getInstance().play(item.getFile(), playBtn, getContext()));
            playBtn.setOnCreateContextMenuListener((m, v, i) -> {
                if (!sampleName.equals(HIT) && !sampleName.equals(IT_JUST_WORKS)) {
                    m.setHeaderTitle(sampleName);

                    MenuItem deleteSampleItem = m.add(0, ID_DELETE, 0, R.string.delete_sample);
                    deleteSampleItem.setOnMenuItemClickListener(menuItem -> {
                        ((MainActivity) getContext()).removeFromSamples(item);
                        return true;
                    });

                    MenuItem editSampleItem = m.add(0, ID_EDIT, 0, R.string.edit_sample);
                    editSampleItem.setOnMenuItemClickListener(menuItem -> {
                        AlertDialog dia = new AlertDialog.Builder(getContext())
                                .setView(R.layout.add_prompt)
                                .setNegativeButton(R.string.cancel, (dialog, which) -> Log.d(TAG, "Edit canceled."))
                                .create();

                        dia.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.okay), (d, w) -> {
                            EditText sampleNameEditText = dia.findViewById(R.id.sampleName);
                            String newName = sampleNameEditText.getText().toString();
                            if (newName.isEmpty()) {
                                newName = getContext().getString(R.string.unnamed_sample);
                            }
                            ((MainActivity) getContext()).editSample(sampleName, newName);
                        });

                        dia.show();
                        return true;
                    });
                }
            });
        }

        return view;
    }

    private void setTextSize(TextView textView) {
        String textSize = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(KEY_TEXT_SIZE, DEFAULT_TEXT_SIZE);
        if (textSize == null) {
            textSize = "1"; //default
        }
        switch (textSize) {
            case "0": //small
                textView.setTextSize(12);
                break;
            case "2": //large
                textView.setTextSize(16);
                break;
            case "3": //huge
                textView.setTextSize(18);
                break;
            case "1": //medium
            default:
                textView.setTextSize(14);
                break;
        }
    }
}
