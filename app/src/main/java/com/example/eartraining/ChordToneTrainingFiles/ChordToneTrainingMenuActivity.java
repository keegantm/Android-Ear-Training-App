package com.example.eartraining.ChordToneTrainingFiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eartraining.R;

import java.util.ArrayList;
import java.util.HashMap;

/*
Menu from which the user can open an activity to edit the quiz parameters
or start the quiz
 */
public class ChordToneTrainingMenuActivity extends AppCompatActivity {

    private Button customizeActivityButton;
    private Button startActivityButton;

    private int questionCount;
    private HashMap<String, Boolean> chords;
    private boolean staticRoot;
    private HashMap<String, Boolean> majTriadChordTones;
    private HashMap<String, Boolean> minTriadChordTones;

    //major chord tone names
    String[] majChordToneKeys = {
            "Root", "3rd", "5th", "♭7th", "7th", "♭9th", "9th", "♯9th", "♯11th", "♭13th", "13th"
    };

    //minor chord tone names
    String[] minChordToneKeys = {
            "Root", "♭3rd", "5th", "♭7th", "7th", "♭9th", "9th", "11th", "♯11th", "♭13th", "13th"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_tone_training_menu);

        customizeActivityButton = findViewById(R.id.chordToneOptionsButton);
        startActivityButton = findViewById(R.id.chordToneStartActivityButton);


        //restore values from config change
        if (savedInstanceState != null) {
            questionCount = savedInstanceState.getInt("numQuestions");
            staticRoot = savedInstanceState.getBoolean("Static Root");
            chords = (HashMap<String, Boolean>) savedInstanceState.getSerializable("chords");
            majTriadChordTones = (HashMap<String, Boolean>) savedInstanceState.getSerializable("majTriadChordTones");
            minTriadChordTones = (HashMap<String, Boolean>) savedInstanceState.getSerializable("minTriadChordTones");

            logValues("TEST", "\nAFTER CONFIG CHANGE :");

        }
        else {
            //load default chord tone settings
            Log.d("TEST", "LOADING DEFAULT SETTINGS");
            loadDefaultSettings();
        }

        customizeActivityButton.setOnClickListener(v -> openOptions());
        startActivityButton.setOnClickListener(v -> startQuiz());
    }

    /*
     Method to create default settings for the chord tone identification
     quiz
      */
    private void loadDefaultSettings() {
        if (chords == null) {
            chords = new HashMap<>(2);
        }
        if (majTriadChordTones == null) {
            majTriadChordTones = new HashMap<>(11);
        }
        if (minTriadChordTones == null) {
            minTriadChordTones = new HashMap<>(11);
        }

        //by default any chord can appear
        chords.put("Major Triad", true);
        chords.put("Minor Triad", true);

        //sharp unicode : \u266F
        //flat unicode : \u266D

        //default major triad chord tone settings.
        //create hashmap in order of adding extensions (root, 3rd, 5th, 7th, 9th, 11th, 13th, ect.)
        majTriadChordTones.put("Root", true);
        majTriadChordTones.put("3rd", true);
        majTriadChordTones.put("5th", true);
        majTriadChordTones.put("♭7th", true);
        majTriadChordTones.put("7th", true);
        majTriadChordTones.put("♭9th", false);
        majTriadChordTones.put("9th", true);
        majTriadChordTones.put("♯9th", false);
        majTriadChordTones.put("♯11th", false);
        majTriadChordTones.put("♭13th", false);
        majTriadChordTones.put("13th", false);

        minTriadChordTones.put("Root", true);
        minTriadChordTones.put("♭3rd", true);
        minTriadChordTones.put("5th", true);
        minTriadChordTones.put("♭7th", true);
        minTriadChordTones.put("7th", true);
        minTriadChordTones.put("♭9th", false);
        minTriadChordTones.put("9th", true);
        minTriadChordTones.put("11th", true);
        minTriadChordTones.put("♯11th", false);
        minTriadChordTones.put("♭13th", false);
        minTriadChordTones.put("13th", false);

        questionCount = 10;
        staticRoot = true;

    }

    /*
    Method to open the options activity and
    send the currently saved options to it
     */
    private void openOptions() {

        Intent intent = new Intent(this, ChordToneOptionsActivity.class);

        // Pass data to ChordToneOptions using intent extras
        intent.putExtra("numQuestions", questionCount);
        intent.putExtra("chords", chords);
        intent.putExtra("Static Root", staticRoot);
        intent.putExtra("majTriadChordTones", majTriadChordTones);
        intent.putExtra("minTriadChordTones", minTriadChordTones);

        // Start ChordToneOptionsActivity
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                int result = activityResult.getResultCode();
                Intent data = activityResult.getData();

                if (result == RESULT_OK) {
                    if (data != null) {
                        // Data returned successfully from options activity
                        HashMap<String, Boolean> chords = (HashMap<String, Boolean>) data.getSerializableExtra("chords");
                        HashMap<String, Boolean> majTriadChordTones = (HashMap<String, Boolean>) data.getSerializableExtra("majTriadChordTones");
                        HashMap<String, Boolean> minTriadChordTones = (HashMap<String, Boolean>) data.getSerializableExtra("minTriadChordTones");


                        //he user should be able to just select one type of chord. Check if the chord is even selected before restoring defaults for it
                        //for now, just toast them.
                        /*
                        Ideal way to do this:
                        User unselects a chord.
                        R view is greyed out and the user cannot change clicks for them
                         */

                        if (Boolean.TRUE.equals(chords.get("Major Triad"))){
                            int majTrueCount = 0;
                            //load default settings if the user selected no chord tones for their selected chords
                            assert majTriadChordTones != null;
                            for (boolean value : majTriadChordTones.values()) {
                                if (value) {
                                    majTrueCount++;
                                }
                            }

                            if (majTrueCount < 2) {
                                if (Boolean.TRUE.equals(chords.get("Major Triad"))) {
                                    Toast.makeText(ChordToneTrainingMenuActivity.this, "Invalid chord tone options for Major Triads, Defaults Restored", Toast.LENGTH_SHORT).show();

                                    majTriadChordTones = new HashMap<>(11);

                                    majTriadChordTones.put("Root", true);
                                    majTriadChordTones.put("3rd", true);
                                    majTriadChordTones.put("5th", true);
                                    majTriadChordTones.put("♭7th", true);
                                    majTriadChordTones.put("7th", true);
                                    majTriadChordTones.put("♭9th", false);
                                    majTriadChordTones.put("9th", true);
                                    majTriadChordTones.put("♯9th", false);
                                    majTriadChordTones.put("♯11th", false);
                                    majTriadChordTones.put("♭13th", false);
                                    majTriadChordTones.put("13th", false);
                                }
                            }
                        }

                        if (Boolean.TRUE.equals(chords.get("Minor Triad"))){
                            //load default settings if the user selected no chord tones for their selected chords
                            int minTrueCount = 0;
                            assert minTriadChordTones != null;
                            for (boolean value : minTriadChordTones.values()) {
                                if (value) {
                                    minTrueCount++;
                                }
                            }
                            if (minTrueCount < 2) {
                                if (Boolean.TRUE.equals(chords.get("Minor Triad"))) {
                                    Toast.makeText(ChordToneTrainingMenuActivity.this, "Invalid chord tone options for Minor Triads, Defaults Restored", Toast.LENGTH_SHORT).show();

                                    minTriadChordTones = new HashMap<>(11);

                                    minTriadChordTones.put("Root", true);
                                    minTriadChordTones.put("♭3rd", true);
                                    minTriadChordTones.put("5th", true);
                                    minTriadChordTones.put("♭7th", true);
                                    minTriadChordTones.put("7th", true);
                                    minTriadChordTones.put("♭9th", false);
                                    minTriadChordTones.put("9th", true);
                                    minTriadChordTones.put("11th", true);
                                    minTriadChordTones.put("♯11th", false);
                                    minTriadChordTones.put("♭13th", false);
                                    minTriadChordTones.put("13th", false);
                                }
                            }
                        }

                        int questionCount = data.getIntExtra("numQuestions", 10);
                        boolean staticRoot = data.getBooleanExtra("Static Root", true);

                        ChordToneTrainingMenuActivity.this.chords = chords;
                        ChordToneTrainingMenuActivity.this.majTriadChordTones = majTriadChordTones;
                        ChordToneTrainingMenuActivity.this.minTriadChordTones = minTriadChordTones;
                        ChordToneTrainingMenuActivity.this.questionCount = questionCount;
                        ChordToneTrainingMenuActivity.this.staticRoot = staticRoot;

                        ChordToneTrainingMenuActivity.this.logValues("RESPONSE TEST", "\nReceived data from options :");

                    }
                    else {
                        Log.d("ERROR", "Error receiving data from options, defaults restored");
                        ChordToneTrainingMenuActivity.this.loadDefaultSettings();
                    }
                }
            }
    );

    /*
    Pass data to the quiz activity and start it
     */
    private void startQuiz() {

        //turn majTriadChordTones and minTriadChordTones into array lists to enforce an ordering of the chord tones
        ArrayList<String> selectedMajorChordTones = new ArrayList<>();
        if (chords.get("Major Triad")) {
            for (String chordToneName : majChordToneKeys) {
                Boolean chordToneState = majTriadChordTones.get(chordToneName);

                if (Boolean.TRUE.equals(chordToneState)) {
                    //add it to the array
                    selectedMajorChordTones.add(chordToneName);
                }
            }
        }

        ArrayList<String> selectedMinorChordTones = new ArrayList<>();
        if (chords.get("Minor Triad")) {
            for (String chordToneName : minChordToneKeys) {
                Boolean chordToneState = minTriadChordTones.get(chordToneName);

                if (Boolean.TRUE.equals(chordToneState)) {
                    //add it to the array
                    selectedMinorChordTones.add(chordToneName);
                }
            }
        }

        Intent intent = new Intent(this, ChordToneIdentificationQuizActivity.class);

        // Pass data to Chord Tone Quiz Activity using intent extras
        intent.putExtra("staticRoot", staticRoot);
        intent.putExtra("numQuestions", questionCount);
        intent.putExtra("chords", chords);

        //at least one of these will not be null, enforced in the ChordToneOptionsActivity
        intent.putExtra("Major Triad Chord Tones", selectedMajorChordTones);
        intent.putExtra("Minor Triad Chord Tones", selectedMinorChordTones);

        // Start quiz activity
        startActivity(intent);

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("numQuestions", questionCount);
        outState.putBoolean("Static Root", staticRoot);
        outState.putSerializable("chords", chords);
        outState.putSerializable("majTriadChordTones", majTriadChordTones);
        outState.putSerializable("minTriadChordTones", minTriadChordTones);
    }

    private void logValues(String tag, String starterName) {
        Log.d(tag, starterName);
        Log.d(tag, "Questions :" + questionCount);
        Log.d(tag, "Static Root :" + staticRoot);

        if (Boolean.TRUE.equals(chords.get("Major Triad"))) {
            Log.d(tag, "Major Triads Enabled");

            for (String chordToneName : majChordToneKeys) {
                Boolean chordToneState = majTriadChordTones.get(chordToneName);
                Log.d(tag, "Major Tone " + chordToneName + " : " + chordToneState);
            }
        }
        else {
            Log.d(tag, "Major Triads Disabled");
        }

        if (Boolean.TRUE.equals(chords.get("Minor Triad"))) {
            Log.d(tag, "Minor Triads Enabled");

            for (String chordToneName : minChordToneKeys) {
                Boolean chordToneState = minTriadChordTones.get(chordToneName);
                Log.d(tag, "Minor Tone " + chordToneName + " : " + chordToneState);
            }
        }
        else {
            Log.d(tag, "Minor Triads Disabled");
        }
    }

}