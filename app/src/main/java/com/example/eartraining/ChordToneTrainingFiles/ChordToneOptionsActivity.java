package com.example.eartraining.ChordToneTrainingFiles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.ChordToneTrainingFiles.ChordToneRView.ChordToneItem;
import com.example.eartraining.ChordToneTrainingFiles.ChordToneRView.ChordToneRViewAdapter;
import com.example.eartraining.R;

import java.util.ArrayList;
import java.util.HashMap;

/*
Activity for the user to customize their chord tone
identification quiz
 */
public class ChordToneOptionsActivity extends AppCompatActivity {
    //major chord tone names
    String[] majTriadChordToneKeys = {
            "Root", "3rd", "5th", "♭7th", "7th", "♭9th", "9th", "♯9th", "♯11th", "♭13th", "13th"
    };

    //minor chord tone names
    String[] minTriadChordToneKeys = {
            "Root", "♭3rd", "5th", "♭7th", "7th", "♭9th", "9th", "11th", "♯11th", "♭13th", "13th"
    };

    //hashmap containing each major triad chord tone, and a boolean for if it is enabled
    HashMap<String, Boolean> majTriadChordToneOptions;
    //hashmap containing each minor triad chord tone, and a boolean for if it is enabled
    HashMap<String, Boolean> minTriadChordToneOptions;
    //hashmap containing each chord type, and a boolean for if it is enabled
    HashMap<String, Boolean> chords;
    int numQuestions;
    boolean staticRoot;


    //major triads in the r view
    ArrayList<ChordToneItem> majTriadChordToneItems;
    //r view adapter
    ChordToneRViewAdapter majTriadViewAdapter;
    //Recycler View
    RecyclerView majTriadRView;

    //minor triads in the r view
    ArrayList<ChordToneItem> minTriadChordToneItems;
    //r view adapter
    ChordToneRViewAdapter minTriadViewAdapter;
    //Recycler View
    RecyclerView minTriadRView;

    RadioGroup questionsRadioGroup;
    SwitchCompat majTriadSwitch;
    SwitchCompat minTriadSwitch;
    SwitchCompat staticFirstNoteSwitch;

    Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_tone_options);

        // Retrieve the passed data from the intent. So, load settings from previous menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("Data Loading Test", "In Load data from sending activity");

            // Extract data from the intent extras
            majTriadChordToneOptions = (HashMap<String, Boolean>) extras.getSerializable("majTriadChordTones");
            minTriadChordToneOptions = (HashMap<String, Boolean>) extras.getSerializable("minTriadChordTones");
            chords = (HashMap<String, Boolean>) extras.getSerializable("chords");
            staticRoot = extras.getBoolean("Static Root");
            numQuestions = extras.getInt("numQuestions");

        }

        //Restore data from config change
        if (savedInstanceState != null) {
            majTriadChordToneOptions = (HashMap<String, Boolean>) savedInstanceState.getSerializable("majTriadChordTones");
            minTriadChordToneOptions = (HashMap<String, Boolean>) savedInstanceState.getSerializable("minTriadChordTones");

            chords = (HashMap<String, Boolean>) savedInstanceState.getSerializable("chords");

            numQuestions = savedInstanceState.getInt("Question Count");
            staticRoot = savedInstanceState.getBoolean("Static Root");

            majTriadChordToneItems = savedInstanceState.getParcelableArrayList("Major Triad List");
            minTriadChordToneItems = savedInstanceState.getParcelableArrayList("Minor Triad List");

            logValues("OPTIONS CONFIG CHANGE", "Options activity, data after config change:");
        }

        //set up array list of major chord tones available for recycler view
        if (savedInstanceState == null || !savedInstanceState.containsKey("majTriadChordTones")) {
            //If not restoring from a previous state, initialize the list of items as a new empty list.
            majTriadChordToneItems = new ArrayList<>();
        }

        //Populate the array of major triad chord tones for the recycler view
        if (majTriadChordToneItems != null && majTriadChordToneItems.isEmpty()) {

            for (String chordToneName : majTriadChordToneKeys) {
                Boolean chordToneState = majTriadChordToneOptions.get(chordToneName);

                ChordToneItem newChordToneItem = new ChordToneItem(chordToneName, chordToneState, "Major Triad");
                majTriadChordToneItems.add(newChordToneItem);
            }
        }

        //set up array list of minor chord tones available for recycler view
        if (savedInstanceState == null || !savedInstanceState.containsKey("minTriadChordTones")) {
            //If not restoring from a previous state, initialize the list of items as a new empty list.
            minTriadChordToneItems = new ArrayList<>();
        }

        //Populate the array of major triad chord tones for the recycler view
        if (minTriadChordToneItems != null && minTriadChordToneItems.isEmpty()) {

            for (String chordToneName : minTriadChordToneKeys) {
                Boolean chordToneState = minTriadChordToneOptions.get(chordToneName);

                ChordToneItem newChordToneItem = new ChordToneItem(chordToneName, chordToneState, "Minor Triad");
                minTriadChordToneItems.add(newChordToneItem);
            }
        }

        //button to send data back to previous activity
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> sendDataBackToCallingActivity());

        setUpRecyclerViews();
        setUpNumQuestionsOption();
        setUpChordTypesOption();
        setUpStaticRootOption();


    }

    /*
    Method to set up the recycler views of the Major & Minor triad chord tone choices
     */
    private void setUpRecyclerViews() {
        majTriadRView = findViewById(R.id.majChordToneRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        majTriadRView.setLayoutManager(layoutManager);
        majTriadViewAdapter = new ChordToneRViewAdapter(majTriadChordToneItems, this);
        majTriadRView.setAdapter(majTriadViewAdapter);

        minTriadRView = findViewById(R.id.minChordToneRecyclerView);
        LinearLayoutManager minLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        minTriadRView.setLayoutManager(minLayoutManager);
        minTriadViewAdapter = new ChordToneRViewAdapter(minTriadChordToneItems, this);
        minTriadRView.setAdapter(minTriadViewAdapter);
    }

    /*
    Method for setting up the question Count option.
    The user has the options 5, 10, 15, and 20

    Selects a button to check based off the questionCount variable,
    then sets up a listener for any future button selections
     */
    private void setUpNumQuestionsOption() {

        //get the radio group
        questionsRadioGroup = findViewById(R.id.numberOfQuestionsRadioGroup);

        // select an initial button to check based on the settings passed in from the previous activity
        switch (numQuestions) {
            case 5:
                questionsRadioGroup.check(R.id.radioButton5);
                break;
            case 10:
                questionsRadioGroup.check(R.id.radioButton10);
                break;
            case 15:
                questionsRadioGroup.check(R.id.radioButton15);
                break;
            case 20:
                questionsRadioGroup.check(R.id.radioButton20);
                break;
        }


        //attach a listener to tell when the selected button changes
        questionsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Get the selected radio button
            RadioButton radioButton = findViewById(checkedId);

            // Get the text of the selected radio button
            String selectedOptionText = radioButton.getText().toString();

            // Parse the text to an integer
            int selectedOption;
            try {
                selectedOption = Integer.parseInt(selectedOptionText);
            } catch (NumberFormatException e) {
                // I'll need to edit the string if this happens
                Log.d("ERROR", "Invalid cast to of radio button integer");
                return;
            }

            //change the question count
            numQuestions = selectedOption;

            Log.d("TEST", "Question count is :" + numQuestions);
        });
    }

    /*
    Method to set up the switches for which chords can appear
     */
    private void setUpChordTypesOption() {
        majTriadSwitch = findViewById(R.id.majTriadSwitch);
        minTriadSwitch = findViewById(R.id.minTriadSwitch);

        //make the switches be loaded in as their previous state
        try {
            Boolean majTriadSwitched = chords.get("Major Triad");
            majTriadSwitch.setChecked(Boolean.TRUE.equals(majTriadSwitched));

            Boolean minTriadSwitched = chords.get("Minor Triad");
            minTriadSwitch.setChecked(Boolean.TRUE.equals(minTriadSwitched));
        }
        catch (NullPointerException e){
            Log.d("ERROR", "Error loading if the Static Root Switch is switched from input options");
        }

        majTriadSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle the switch state change
            if (isChecked) {
                // Switch is checked
                chords.put("Major Triad", true);
            } else {
                //must make sure one switch is always checked
                if (!minTriadSwitch.isChecked()) {
                    majTriadSwitch.setChecked(true);
                    chords.put("Major Triad", true);
                }
                else {
                    chords.put("Major Triad", false);
                }
            }
        });

        minTriadSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle the switch state change
            if (isChecked) {
                // Switch is checked
                chords.put("Minor Triad", true);
            } else {
                //must make sure one switch is always checked
                if (!majTriadSwitch.isChecked()) {
                    minTriadSwitch.setChecked(true);
                    chords.put("Minor Triad", true);
                }
                else {
                    chords.put("Minor Triad", false);
                }
            }
        });
    }

    /*
    Method for setting up the static root option.
    The user can either select or deselect this option.

    Selects an initial state based off the staticRoot variable,
    then sets up a listener for any future changes
     */
    private void setUpStaticRootOption() {
        staticFirstNoteSwitch = findViewById(R.id.staticRootSwitch);

        //make the switch be Switched depending on the options when the view is created
        try {
            staticFirstNoteSwitch.setChecked(Boolean.TRUE.equals(staticRoot));
        }
        catch (NullPointerException e){
            Log.d("ERROR", "Error loading if the Static Root Switch is switched from input options");
        }

        //add an onClick listener to the switch, to change the boolean option that will be
        //passed back to the previous activity
        staticFirstNoteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> staticRoot = isChecked);
    }

    /*
    Method to update the values of the major and minor chord tones hashmaps.
     */
    private void updateChordHashMaps() {
        if (chords.get("Major Triad")) {
            for (ChordToneItem majTriadChordTone : majTriadChordToneItems) {
                String chordToneName = majTriadChordTone.getTitle();
                Boolean chordToneState = majTriadChordTone.getEnabled();

                majTriadChordToneOptions.put(chordToneName, chordToneState);
            }
        }

        if (chords.get("Minor Triad")) {
            for (ChordToneItem minTriadChordTone : minTriadChordToneItems) {
                String chordToneName = minTriadChordTone.getTitle();
                Boolean chordToneState = minTriadChordTone.getEnabled();

                minTriadChordToneOptions.put(chordToneName, chordToneState);
            }
        }
    }

    /*
    Method to send options data back to the previous activity.
     */
    private void sendDataBackToCallingActivity() {
        logValues("OPTIONS TEST","\nBefore Update Hashmaps :");
        updateChordHashMaps();

        Intent resultIntent = new Intent();

        //Put data in the intent
        resultIntent.putExtra("numQuestions", numQuestions);
        resultIntent.putExtra("Static Root", staticRoot);
        resultIntent.putExtra("chords", chords);
        resultIntent.putExtra("majTriadChordTones", majTriadChordToneOptions);
        resultIntent.putExtra("minTriadChordTones", minTriadChordToneOptions);

        //send data to previous activity
        setResult(Activity.RESULT_OK, resultIntent);

        finish(); // Finish this activity to return to the calling activity
    }

    /*
    Method to save the state in the case of configuration changes.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //save the list of major chord array items
        outState.putParcelableArrayList("Major Triad List", majTriadChordToneItems);
        //save the list of minor chord array items
        outState.putParcelableArrayList("Minor Triad List", minTriadChordToneItems);

        //save what chords the user has selected
        outState.putSerializable("chords", chords);
        //save hashmap of maj triad chord tones
        outState.putSerializable("majTriadChordTones", majTriadChordToneOptions);
        //save hashmap of min triad chord tones
        outState.putSerializable("minTriadChordTones", minTriadChordToneOptions);

        //save question count
        outState.putInt("Question Count", numQuestions);
        //save if the root is fixed
        outState.putBoolean("Static Root", staticRoot);

    }

    /*
    Helper method for logging the user's selected options.
     */
    private void logValues(String tag, String starterName) {
        Log.d(tag, starterName);
        Log.d(tag, "Questions :" + numQuestions);
        Log.d(tag, "Static Root :" + staticRoot);

        if (Boolean.TRUE.equals(chords.get("Major Triad"))) {
            Log.d(tag, "Major Triads Enabled");

            for (ChordToneItem chordTone: majTriadChordToneItems) {
                Log.d(tag, "Major Tone " + chordTone.getTitle() + " : " + chordTone.getEnabled());
            }
        }
        else {
            Log.d(tag, "Major Triads Disabled");
        }

        if (Boolean.TRUE.equals(chords.get("Minor Triad"))) {
            Log.d(tag, "Minor Triads Enabled");

            for (ChordToneItem chordTone: minTriadChordToneItems) {
                Log.d(tag, "Minor Tone " + chordTone.getTitle() + " : " + chordTone.getEnabled());
            }
        }
        else {
            Log.d(tag, "Minor Triads Disabled");
        }
    }


}