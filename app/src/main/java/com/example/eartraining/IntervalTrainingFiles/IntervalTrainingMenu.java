package com.example.eartraining.IntervalTrainingFiles;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.eartraining.R;

/*
Menu from which the user can open an activity to edit the quiz parameters
or start the quiz
 */
public class IntervalTrainingMenu extends AppCompatActivity {

    //booleans to get:
    // same root or not
    // boolean for each interval
    HashMap<String, Boolean> booleanOptions;

    /*
    Boolean Options:
    staticRoot - Is the first played note always the same
    unison
    m2  - minor 2nd
    M2  - major 2nd
    m3
    M3
    p4  - perfect 4th
    tt  - tritone
    p5
    m6
    M6
    m7
    M7
    octave
    m9
    M9
     */

    String intervalDirection;    //Either ascending, descending, or both

    // how many questions there are in the quiz
    int questionCount;

    Button optionButton;
    Button startActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_training_menu);

        //restore values from config change
        if (savedInstanceState != null) {
            questionCount = savedInstanceState.getInt("numQuestions");
            intervalDirection = savedInstanceState.getString("intervalDirection");
            booleanOptions = (HashMap<String, Boolean>) savedInstanceState.getSerializable("booleanOptions");
        }
        else {
            //load default interval settings
            loadDefaultSettings();
        }

        optionButton = findViewById(R.id.intervalOptionsButton);
        startActivityButton = findViewById(R.id.intervalStartActivityButton);

        optionButton.setOnClickListener(v -> openOptions());
        startActivityButton.setOnClickListener(v -> startQuiz());
    }

    /*
    Method to create default settings for the interval
    quiz
     */
    private void loadDefaultSettings() {
        if (booleanOptions == null) {
            booleanOptions = new HashMap<>(16);
        }
        intervalDirection = "ascending";
        questionCount = 10;

        //have diatonic major scale intervals be default
        booleanOptions.put("unison", true);
        booleanOptions.put("m2", false);
        booleanOptions.put("M2", true);
        booleanOptions.put("m3", false);
        booleanOptions.put("M3", true);
        booleanOptions.put("p4", true);
        booleanOptions.put("tt", false);
        booleanOptions.put("p5", true);
        booleanOptions.put("m6", false);
        booleanOptions.put("M6", true);
        booleanOptions.put("m7", false);
        booleanOptions.put("M7", true);
        booleanOptions.put("octave", true);
        booleanOptions.put("m9", false);
        booleanOptions.put("M9", true);


        //also make the root static as default
        booleanOptions.put("Static Root", true);
    }

    /*
    Method to open the options activity and
    send the currently saved options to it
     */
    private void openOptions() {

        Intent intent = new Intent(this, IntervalOptionsActivity.class);

        // Pass data to IntervalOptionsActivity using intent extras
        intent.putExtra("booleanHashmap", booleanOptions);
        intent.putExtra("intervalDirection", intervalDirection);
        intent.putExtra("numQuestions", questionCount);

        // Start IntervalOptionsActivity
        activityResultLauncher.launch(intent);
    }

    /*
    Helper method to log the interval options
     */
    private void printOptions() {
        try {
            String[] intervalNames = {
                    "Unison", "Minor Second", "Major Second", "Minor Third", "Major Third",
                    "Perfect Fourth", "Tritone", "Perfect Fifth", "Minor Sixth", "Major Sixth",
                    "Minor Seventh", "Major Seventh", "Octave", "Minor Ninth", "Major Ninth"
            };

            String[] intervalKeys = {"unison", "m2", "M2", "m3", "M3", "p4", "tt", "p5", "m6", "M6", "m7", "M7", "octave", "m9", "M9"};

            Log.d("Response Test", "Questions : " + String.valueOf(questionCount));
            Log.d("Response Test", "Static Root : " + String.valueOf(booleanOptions.get("Static Root")));
            Log.d("Response Test", "Interval Direction : " + intervalDirection);

            for (int i = 0; i < intervalKeys.length; i++) {
                String intervalName = intervalNames[i];
                Boolean intervalState = booleanOptions.get(intervalKeys[i]);

                Log.d("Response Test", intervalName + " : " + String.valueOf(intervalState));

            }

        } catch (Exception e) {
            Log.d("ERROR", "Something went wrong printing Interval Options Responses");
        }

    }

    /*
    Method to receive data from the options activity.

    referenced https://www.youtube.com/watch?v=-y5eF0u1bZQ
     */

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                int result = activityResult.getResultCode();
                Intent data = activityResult.getData();

                if (result == RESULT_OK) {
                    if (data != null) {
                        String[] intervalKeys = {"unison", "m2", "M2", "m3", "M3", "p4", "tt", "p5", "m6", "M6", "m7", "M7", "octave", "m9", "M9"};


                        // Data returned successfully from IntervalOptionsActivity
                        HashMap<String, Boolean> booleanOptions = (HashMap<String, Boolean>) data.getSerializableExtra("booleanHashmap");
                        String intervalDirection = data.getStringExtra("intervalDirection");
                        int questionCount = data.getIntExtra("numQuestions", 10); // Provide default value if necessary

                        //load default settings if the user selected no or only one intervals
//                        int trueCount = 0;
//                        for (boolean value : booleanOptions.values()) {
//                            if (value) {
//                                trueCount++;
//                            }
//                        }
                        int trueCount = 0;
                        for (String key: intervalKeys) {
                            if (booleanOptions.get(key)) {
                                trueCount++;
                            }
                        }

                        Log.d("DEBUG", "Interval Option count : " + trueCount);
                        if (booleanOptions == null || trueCount < 2) {
                            Toast.makeText(IntervalTrainingMenu.this, "Invalid interval options, defaults restored", Toast.LENGTH_SHORT).show();

                            booleanOptions = new HashMap<>(16);

                            booleanOptions.put("unison", true);
                            booleanOptions.put("m2", false);
                            booleanOptions.put("M2", true);
                            booleanOptions.put("m3", false);
                            booleanOptions.put("M3", true);
                            booleanOptions.put("p4", true);
                            booleanOptions.put("tt", false);
                            booleanOptions.put("p5", true);
                            booleanOptions.put("m6", false);
                            booleanOptions.put("M6", true);
                            booleanOptions.put("m7", false);
                            booleanOptions.put("M7", true);
                            booleanOptions.put("octave", true);
                            booleanOptions.put("m9", false);
                            booleanOptions.put("M9", true);
                        }

                        IntervalTrainingMenu.this.booleanOptions = booleanOptions;
                        IntervalTrainingMenu.this.intervalDirection = intervalDirection;
                        IntervalTrainingMenu.this.questionCount = questionCount;

                        IntervalTrainingMenu.this.printOptions();

                    }
                    else {
                        Log.d("ERROR", "No Data Received, defaults restored");
                        IntervalTrainingMenu.this.loadDefaultSettings();
                        }
                    }
                }
    );

    /*
    Method to pass the quiz options to the quiz activity and start it
     */
    private void startQuiz() {
        String[] intervalNames = {
                "Unison", "Minor Second", "Major Second", "Minor Third", "Major Third",
                "Perfect Fourth", "Tritone", "Perfect Fifth", "Minor Sixth", "Major Sixth",
                "Minor Seventh", "Major Seventh", "Octave", "Minor Ninth", "Major Ninth"
        };

        String[] intervalKeys = {"unison", "m2", "M2", "m3", "M3", "p4", "tt", "p5", "m6", "M6", "m7", "M7", "octave", "m9", "M9"};

        //create arraylist of selected interval names
        ArrayList<String> selectedIntervals = new ArrayList<>();
        for (int i = 0; i < intervalKeys.length; i++) {
            String intervalName = intervalNames[i];
            Boolean intervalState = booleanOptions.get(intervalKeys[i]);

            if (Boolean.TRUE.equals(intervalState)) {
                //add it to the array
                selectedIntervals.add(intervalName);
            }
        }

        Intent intent = new Intent(this, IntervalIdentificationQuizActivity.class);

        // Pass data to IntervalOptionsActivity using intent extras
        intent.putExtra("staticRoot", booleanOptions.get("Static Root"));
        intent.putExtra("intervalDirection", intervalDirection);
        intent.putExtra("numQuestions", questionCount);
        intent.putExtra("selectedIntervals", selectedIntervals);


        // Start IntervalOptionsActivity
        activityResultLauncher.launch(intent);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("numQuestions", questionCount);
        outState.putString("intervalDirection", intervalDirection);
        outState.putSerializable("booleanOptions", booleanOptions);
    }
}