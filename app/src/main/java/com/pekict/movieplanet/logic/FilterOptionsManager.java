package com.pekict.movieplanet.logic;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.presentation.MainActivity;

import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class FilterOptionsManager {
    private static final String TAG_NAME = FilterOptionsManager.class.getSimpleName();
    public static final String SORT = "SORT";
    public static final String ACTION = "28";
    public static final String HORROR = "27";
    public static final String COMEDY = "35";
    public static final String THRILLER = "53";
    public static final String SCIFI = "878";
    public static final String DRAMA = "18";
    public static final String ROMANCE = "10749";
    public static final String DOCUMENTARY = "99";
    public static final String ALLGENRES = "ALLGENRES";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String RATING = "RATING";

    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mSharedPrefsEditor;
    private Map<String, String> mFilterOptions;

    public FilterOptionsManager(SharedPreferences sharedPrefs, SharedPreferences.Editor sharedPrefsEditor, MainActivity mainActivity) {

        mSharedPrefs = sharedPrefs;
        mSharedPrefsEditor = sharedPrefsEditor;
        mFilterOptions = new HashMap<>();

        initFilterOptions();
    }

    public String getQuery() {
        return getQueryFromRadioText(mFilterOptions.get(SORT));
    }

    // Function that returns a Query based on the given input
    public String getQueryFromRadioText(String input) {
        String query = "";

        switch (input) {
            case "Title (A-Z)":
                query = "original_title.asc";
                break;
            case "Title (Z-A)":
                query = "original_title.desc";
                break;
            case "Popularity (ASC)":
                query = "popularity.asc";
                break;
            case "Popularity (DESC)":
                query = "popularity.desc";
                break;
            case "Rating (ASC)":
                query = "vote_average.asc";
                break;
            case "Rating (DESC)":
                query = "vote_average.desc";
                break;
            case "Release Date (ASC)":
                query = "release_date.asc";
                break;
            case "Release Date (DESC)":
                query = "release_date.desc";
                break;
        }

        return query;
    }

    // Function that checks if object contains all keys to prevent NullPointerExceptions
    public boolean filterOptionsExists() {
        return mFilterOptions.containsKey(SORT) && mFilterOptions.containsKey(ACTION) && mFilterOptions.containsKey(HORROR)
                && mFilterOptions.containsKey(COMEDY) && mFilterOptions.containsKey(THRILLER) && mFilterOptions.containsKey(SCIFI)
                && mFilterOptions.containsKey(DRAMA) && mFilterOptions.containsKey(ROMANCE) && mFilterOptions.containsKey(DOCUMENTARY)
                && mFilterOptions.containsKey(ALLGENRES) && mFilterOptions.containsKey(LANGUAGE) && mFilterOptions.containsKey(RATING);
    }

    private boolean filterAllGenres() {
        return mFilterOptions.get(ACTION).equals("false") && mFilterOptions.get(HORROR).equals("false")
                && mFilterOptions.get(COMEDY).equals("false") && mFilterOptions.get(THRILLER).equals("false")
                && mFilterOptions.get(SCIFI).equals("false") && mFilterOptions.get(DRAMA).equals("false")
                && mFilterOptions.get(ROMANCE).equals("false") && mFilterOptions.get(DOCUMENTARY).equals("false");
    }

    public void initFilterOptions() {
        mFilterOptions.put(SORT, mSharedPrefs.getString(SORT, "Popularity (DESC)"));
        mFilterOptions.put(ACTION, mSharedPrefs.getString(ACTION, "false"));
        mFilterOptions.put(HORROR, mSharedPrefs.getString(HORROR, "false"));
        mFilterOptions.put(COMEDY, mSharedPrefs.getString(COMEDY, "false"));
        mFilterOptions.put(THRILLER, mSharedPrefs.getString(THRILLER, "false"));
        mFilterOptions.put(SCIFI, mSharedPrefs.getString(SCIFI, "false"));
        mFilterOptions.put(DRAMA, mSharedPrefs.getString(DRAMA, "false"));
        mFilterOptions.put(ROMANCE, mSharedPrefs.getString(ROMANCE, "false"));
        mFilterOptions.put(DOCUMENTARY, mSharedPrefs.getString(DOCUMENTARY, "false"));
        mFilterOptions.put(ALLGENRES, mSharedPrefs.getString(ALLGENRES, "true"));
        mFilterOptions.put(LANGUAGE, mSharedPrefs.getString(LANGUAGE, "All"));
        mFilterOptions.put(RATING, mSharedPrefs.getString(RATING, "All"));
    }

    public Map<String, String> getFilterOptions() {
        return mFilterOptions;
    }

    public void setSortOptions(View sortView) {
        RadioGroup radioGroup = sortView.findViewById(R.id.radio_group);
        View radioButtonView = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(radioGroup.indexOfChild(radioButtonView));

        mFilterOptions.put(SORT, radioButton.getText().toString());
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
        Spinner languageSpinner = filterView.findViewById(R.id.filter_language);
        Spinner ratingSpinner = filterView.findViewById(R.id.filter_rating);

        mFilterOptions.put(ACTION, String.valueOf(actionBox.isChecked()));
        mFilterOptions.put(HORROR, String.valueOf(horrorBox.isChecked()));
        mFilterOptions.put(COMEDY, String.valueOf(comedyBox.isChecked()));
        mFilterOptions.put(THRILLER, String.valueOf(thrillerBox.isChecked()));
        mFilterOptions.put(SCIFI, String.valueOf(scifiBox.isChecked()));
        mFilterOptions.put(DRAMA, String.valueOf(dramaBox.isChecked()));
        mFilterOptions.put(ROMANCE, String.valueOf(romanceBox.isChecked()));
        mFilterOptions.put(DOCUMENTARY, String.valueOf(documentaryBox.isChecked()));
        mFilterOptions.put(ALLGENRES, String.valueOf(filterAllGenres()));
        mFilterOptions.put(LANGUAGE, languageSpinner.getSelectedItem().toString());
        mFilterOptions.put(RATING, ratingSpinner.getSelectedItem().toString());
    }

    public void updateSortOptions(View sortView) {
        setSortOptions(sortView);
        saveSortOption();
    }

    public void updateFilterOptions(View filterView) {
        setFilterOptions(filterView);
        saveFilterOptions();
    }

    // Function to save the users sort option as SharedPreferences
    private void saveSortOption() {
        if (mSharedPrefsEditor == null) { return; }
        if (!filterOptionsExists()) { return; }

        mSharedPrefsEditor.putString(SORT, mFilterOptions.get(SORT));

        mSharedPrefsEditor.apply();
    }

    // Function to save the users filter options as SharedPreferences
    public void saveFilterOptions() {
        if (mSharedPrefsEditor == null) { return; }
        if (!filterOptionsExists()) { return; }

        mSharedPrefsEditor.putString(ACTION, mFilterOptions.get(ACTION));
        mSharedPrefsEditor.putString(HORROR, mFilterOptions.get(HORROR));
        mSharedPrefsEditor.putString(COMEDY, mFilterOptions.get(COMEDY));
        mSharedPrefsEditor.putString(THRILLER, mFilterOptions.get(THRILLER));
        mSharedPrefsEditor.putString(SCIFI, mFilterOptions.get(SCIFI));
        mSharedPrefsEditor.putString(DRAMA, mFilterOptions.get(DRAMA));
        mSharedPrefsEditor.putString(ROMANCE, mFilterOptions.get(ROMANCE));
        mSharedPrefsEditor.putString(DOCUMENTARY, mFilterOptions.get(DOCUMENTARY));
        mSharedPrefsEditor.putString(ALLGENRES, mFilterOptions.get(ALLGENRES));
        mSharedPrefsEditor.putString(LANGUAGE, mFilterOptions.get(LANGUAGE));
        mSharedPrefsEditor.putString(RATING, mFilterOptions.get(RATING));

        mSharedPrefsEditor.apply();

        Log.d(TAG_NAME, "SharedPrefs opgeslagen!");
    }

    public void setSortMenuUI(View sortView) {
        RadioGroup radioGroup = sortView.findViewById(R.id.radio_group);
        String sortOptionString = mFilterOptions.get(SORT);

        for (int index = 0; index < radioGroup.getChildCount(); index++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);

            if (radioButton.getText().toString().equals(sortOptionString)) {
                radioButton.setChecked(true);
                return;
            }
        }
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
        Spinner languageSpinner = filterView.findViewById(R.id.filter_language);
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
        ArrayAdapter myAdap = (ArrayAdapter) languageSpinner.getAdapter();
        int languagePosition = myAdap.getPosition(mFilterOptions.get(LANGUAGE));
        languageSpinner.setSelection(languagePosition);

        // Setting the Spinners to the saved String position in the String-Arrays
        myAdap = (ArrayAdapter) ratingSpinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(mFilterOptions.get(RATING));
        ratingSpinner.setSelection(spinnerPosition);
    }
}
