package sh.lrk.soundboard;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Comparator;

import static sh.lrk.soundboard.settings.SettingsActivity.DEFAULT_TEXT_SIZE;
import static sh.lrk.soundboard.settings.SettingsActivity.KEY_TEXT_SIZE;

class SampleAdapter extends ArrayAdapter<SoundboardSample> {

    private static final int ITEM_VIEW = R.layout.sample_list_item;

    SampleAdapter(Context context) {
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

            TextView sampleName = view.findViewById(R.id.sampleName);
            ImageButton playBtn = view.findViewById(R.id.playBtn);

            setTextSize(sampleName);
            sampleName.setText(item.getName());

            playBtn.setOnClickListener(v -> PlaybackHandler.getInstance().play(item.getFile()));
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
