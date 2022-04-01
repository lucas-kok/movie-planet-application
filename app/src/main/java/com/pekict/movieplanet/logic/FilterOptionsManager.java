package com.pekict.movieplanet.logic;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.presentation.MainActivity;

import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class FilterOptionsManager {
    private static final String TAG_NAME = FilterOptionsManager.class.getSimpleName();
    public static final String ACTION = "28";
    public static final String HORROR = "27";
    public static final String COMEDY = "35";
    public static final String THRILLER = "53";
    public static final String SCIFI = "878";
    public static final String DRAMA = "18";
    public static final String ROMANCE = "10749";
    public static final String DOCUMENTARY = "99";
    public static final String RATING = "RATING";

    private MainActivity mainActivity;

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;
    private Map<String, String> mFilterOptions;

    public FilterOptionsManager(SharedPreferences sharedPrefs, SharedPreferences.Editor sharedPrefsEditor, MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        mSharedPrefs = sharedPrefs;
        mSharedPrefsEditor = sharedPrefsEditor;
        mFilterOptions = new HashMap<>();

        initFilterOptions();
    }

    public void initFilterOptions() {
        mFilterOptions.put(ACTION, mSharedPrefs.getString(ACTION, "true"));
        mFilterOptions.put(HORROR, mSharedPrefs.getString(HORROR, "true"));
        mFilterOptions.put(COMEDY, mSharedPrefs.getString(COMEDY, "true"));
        mFilterOptions.put(THRILLER, mSharedPrefs.getString(THRILLER, "true"));
        mFilterOptions.put(SCIFI, mSharedPrefs.getString(SCIFI, "true"));
        mFilterOptions.put(DRAMA, mSharedPrefs.getString(DRAMA, "true"));
        mFilterOptions.put(ROMANCE, mSharedPrefs.getString(ROMANCE, "true"));
        mFilterOptions.put(DOCUMENTARY, mSharedPrefs.getString(DOCUMENTARY, "true"));
        mFilterOptions.put(RATING, mSharedPrefs.getString(RATING, "5"));
    }

    // Function that checks if object contains all keys to prevent NullPointerExceptions
    public boolean filterOptionsExists() {
        return mFilterOptions.containsKey(ACTION) && mFilterOptions.containsKey(HORROR)
                && mFilterOptions.containsKey(COMEDY) && mFilterOptions.containsKey(THRILLER)
                && mFilterOptions.containsKey(SCIFI) && mFilterOptions.containsKey(DRAMA) && mFilterOptions.containsKey(ROMANCE)
                && mFilterOptions.containsKey(DOCUMENTARY);
    }

    public Map<String, String> getFilterOptions() {
        return mFilterOptions;
    }

    public void setFilterOptions(View filterView) {
        CheckBox actionBox = filterView.findViewById(R.id.filter_genre_action);
        CheckBox horrorBox = filterView.findViewById(R.id.filter_genre_horror);
        CheckBox comedyBox = filterView.findViewById(R.id.filter_genre_comedy);
        CheckBox thrillerBox = filterView.findViewById(R.id.filter_genre_thriller);
        CheckBox scifiBox = filterView.findViewById(R.id.filter_genre_scifi);
        CheckBox dramaBox = filterView.findViewById(R.id.filter_genre_drama);
        CheckBox romanceBox = filterView.findViewById(R.id.filter_genre_romance);
        CheckBox documentaryBox = filterView.findViewById(R.id.filter_genre_documentary);
        Spinner ratingSpinner = filterView.findViewById(R.id.filter_rating);

        Log.d(TAG_NAME, ratingSpinner.getSelectedItem().toString());

        mFilterOptions.put(ACTION, String.valueOf(actionBox.isChecked()));
        mFilterOptions.put(HORROR, String.valueOf(horrorBox.isChecked()));
        mFilterOptions.put(COMEDY, String.valueOf(comedyBox.isChecked()));
        mFilterOptions.put(THRILLER, String.valueOf(thrillerBox.isChecked()));
        mFilterOptions.put(SCIFI, String.valueOf(scifiBox.isChecked()));
        mFilterOptions.put(DRAMA, String.valueOf(dramaBox.isChecked()));
        mFilterOptions.put(ROMANCE, String.valueOf(romanceBox.isChecked()));
        mFilterOptions.put(DOCUMENTARY, String.valueOf(documentaryBox.isChecked()));
        mFilterOptions.put(RATING, ratingSpinner.getSelectedItem().toString());
    }

    public void updateFilterOptions(View filterView) {
        setFilterOptions(filterView);
        saveFilterOptions();
    }

    // Function to save the users filter options as SharedPreferences
    public void saveFilterOptions() {
        if (mSharedPrefsEditor == null) {
            return;
        }

        if (!filterOptionsExists()) {
            return;
        }

        Log.d(TAG_NAME, mFilterOptions.get(ACTION));
        mSharedPrefsEditor.putString(ACTION, mFilterOptions.get(ACTION));
        mSharedPrefsEditor.putString(HORROR, mFilterOptions.get(HORROR));
        mSharedPrefsEditor.putString(COMEDY, mFilterOptions.get(COMEDY));
        mSharedPrefsEditor.putString(THRILLER, mFilterOptions.get(THRILLER));
        mSharedPrefsEditor.putString(SCIFI, mFilterOptions.get(SCIFI));
        mSharedPrefsEditor.putString(DRAMA, mFilterOptions.get(DRAMA));
        mSharedPrefsEditor.putString(ROMANCE, mFilterOptions.get(ROMANCE));
        mSharedPrefsEditor.putString(DOCUMENTARY, mFilterOptions.get(DOCUMENTARY));
        mSharedPrefsEditor.putString(RATING, mFilterOptions.get(RATING));

        mSharedPrefsEditor.apply();

        Log.d(TAG_NAME, "SharedPrefs opgeslagen!");
    }

    public void setFilterMenuUI(View filterView) {
        CheckBox actionBox = filterView.findViewById(R.id.filter_genre_action);
        CheckBox horrorBox = filterView.findViewById(R.id.filter_genre_horror);
        CheckBox comedyBox = filterView.findViewById(R.id.filter_genre_comedy);
        CheckBox thrillerBox = filterView.findViewById(R.id.filter_genre_thriller);
        CheckBox scifiBox = filterView.findViewById(R.id.filter_genre_scifi);
        CheckBox dramaBox = filterView.findViewById(R.id.filter_genre_drama);
        CheckBox romanceBox = filterView.findViewById(R.id.filter_genre_romance);
        CheckBox documentaryBox = filterView.findViewById(R.id.filter_genre_documentary);
        Spinner ratingSpinner = filterView.findViewById(R.id.filter_rating);

        actionBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(ACTION)));
        horrorBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(HORROR)));
        comedyBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(COMEDY)));
        thrillerBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(THRILLER)));
        scifiBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(SCIFI)));
        dramaBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(DRAMA)));
        romanceBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(ROMANCE)));
        documentaryBox.setChecked(Boolean.parseBoolean(mFilterOptions.get(DOCUMENTARY)));

        // Setting the Spinners to the saved String position in the String-Arrays
        ArrayAdapter myAdap = (ArrayAdapter) ratingSpinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(mFilterOptions.get(RATING));
        ratingSpinner.setSelection(spinnerPosition);
    }
}
