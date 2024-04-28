package com.example.eartraining.IntervalTrainingFiles;

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

import com.example.eartraining.IntervalTrainingFiles.IntervalSelectionRView.IntervalItem;
import com.example.eartraining.IntervalTrainingFiles.IntervalSelectionRView.IntervalRViewAdaptor;
import com.example.eartraining.R;

import java.util.ArrayList;
import java.util.HashMap;





/*
Activity for the user to customize their interval
identification quiz
 */
public class IntervalOptionsActivity extends AppCompatActivity {

    //keys of the intervals stored in a hashmap
    String[] intervalKeys = {"unison", "m2", "M2", "m3", "M3", "p4", "tt", "p5", "m6", "M6", "m7", "M7", "octave", "m9", "M9"};
    //names of the intervals
    String[] intervalNames = {
            "Unison", "Minor Second", "Major Second", "Minor Third", "Major Third",
            "Perfect Fourth", "Tritone", "Perfect Fifth", "Minor Sixth", "Major Sixth",
            "Minor Seventh", "Major Seventh", "Octave", "Minor Ninth", "Major Ninth"
    };

    //intervals in the r view
    ArrayList<IntervalItem> intervals;
    //r view adapter
    IntervalRViewAdaptor viewAdapter;
    //Recycler View
    RecyclerView rView;


    //Options for if there is a static root, and for each interval
    HashMap<String, Boolean> booleanOptions;
    //options for directions of intervals
    String intervalDirection;    //Either ascending, descending, or both
    //options for how many questions will be on the quiz
    int questionCount;


    //radio group of Question Count
    RadioGroup questionsRadioGroup;

    //radio group of interval direction (ascending, descending, both)
    RadioGroup intervalDirectionRadioGroup;

    //switch that controls if there is a static first note
    SwitchCompat staticFirstNoteSwitch;


    //button to submit options
    Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_options);

        //LOAD DATA FROM PREVIOUS ACTIVITY
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("Data Loading Test", "In Load data from sending activity");

            // Extract data from the intent extras
            booleanOptions = (HashMap<String, Boolean>) extras.getSerializable("booleanHashmap");
            intervalDirection = extras.getString("intervalDirection");
            questionCount = extras.getInt("numQuestions");

        }

        //ADDED THIS
        // Restore from config change
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("booleanOptions")) {
                booleanOptions = (HashMap<String, Boolean>) savedInstanceState.getSerializable("booleanOptions");
            }

            questionCount = savedInstanceState.getInt("Question Count");

            if (savedInstanceState.containsKey("Interval Direction")) {
                intervalDirection = savedInstanceState.getString("Interval Direction");
            }
        }

        //set up array list of intervals
        if (savedInstanceState == null || !savedInstanceState.containsKey("Intervals List")) {
            //If first time opening, initialize the list of items as a new empty list.
            intervals = new ArrayList<>();
        } else {
            //If Restoring a previous state, get the list from the bundle
            intervals = savedInstanceState.getParcelableArrayList("Intervals List");
        }


        //Populate the array of intervals for the recycler view
        if (intervals != null && intervals.isEmpty()) {

            for (int i = 0; i < intervalKeys.length; i++) {
                String intervalName = intervalNames[i];
                Boolean intervalState = booleanOptions.get(intervalKeys[i]);

                IntervalItem newInterval = new IntervalItem(intervalName, intervalState);
                intervals.add(newInterval);
            }
        }

        //set up recycler view of interval type options
        rView = findViewById(R.id.intervalRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rView.setLayoutManager(layoutManager);
        viewAdapter = new IntervalRViewAdaptor(intervals, this);
        rView.setAdapter(viewAdapter); // Make sure to set the adapter to the RecyclerView


        //button to send data back to previous activity
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> sendDataBackToCallingActivity());

        //set up the number of questions option
        setUpQuestionCountOption();
        //set up the static root option
        setUpStaticRootOption();
        //set up the interval direction option
        setUpIntervalDirection();

    }

    /*
    Method for setting up the question Count option.
    The user has the options 5, 10, 15, and 20

    Selects a button to check based off the questionCount variable,
    then sets up a listener for any future button selections
     */
    private void setUpQuestionCountOption() {

        //get the radio group
        questionsRadioGroup = findViewById(R.id.numberOfQuestionsRadioGroup);

        // select an initial button to check based on the settings passed in from the previous activity
        switch (questionCount) {
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
            questionCount = selectedOption;

            Log.d("TEST", "Question count is :" + questionCount);
        });

    }

    /*
    Method for setting up the static root option.
    The user can either select or deselect this option.

    Selects an initial state based off booleanOptions.get("Static Root"),
    then sets up a listener for any future changes
     */
    private void setUpStaticRootOption() {
        staticFirstNoteSwitch = findViewById(R.id.staticRootSwitch);

        //make the switch be Switched depending on the options when the view is created
        try {
            Boolean switched = booleanOptions.get("Static Root");

            staticFirstNoteSwitch.setChecked(Boolean.TRUE.equals(switched));
        }
        catch (NullPointerException e){
            Log.d("ERROR", "Error loading if the Static Root Switch is switched from input options");
        }

        //add an onClick listener to the switch, to change the boolean option that will be
        //passed back to the previous activity
        staticFirstNoteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle the switch state change
            if (isChecked) {
                // Switch is checked
                booleanOptions.put("Static Root", true);
            } else {
                // Switch is unchecked
                booleanOptions.put("Static Root", false);
            }
        });
    }

    /*
    Method for setting up the interval direction option.
    The user has the options ascending, descending, and both

    Selects a button to check based off the intervalDirection variable,
    then sets up a listener for any future button selections
     */
    private void setUpIntervalDirection() {
        intervalDirectionRadioGroup = findViewById(R.id.intervalDirectionRadioGroup);

        Log.d("DEBUG", "In Interval Direction setup, value is :" + intervalDirection);

        // Set the selected radio button based on the value of intervalDirection
        switch (intervalDirection) {
            case "ascending":
                intervalDirectionRadioGroup.check(R.id.radioButtonAscending);
                break;
            case "descending":
                intervalDirectionRadioGroup.check(R.id.radioButtonDescending);
                break;
            case "both":
                intervalDirectionRadioGroup.check(R.id.radioButtonBoth);
                Log.d("DEBUG", "Checked both button");
                break;
        }

        intervalDirectionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Get the selected radio button
            RadioButton radioButton = findViewById(checkedId);

            // Get the text of the selected radio button
            intervalDirection = radioButton.getText().toString().toLowerCase();

            Log.d("TEST", "Interval Direction is :" + intervalDirection);
        });
    }

    /*
    Function to update the status of intervals in the HashMap of Boolean options
    Called before sending this hashmap back to the previous activity
     */
    private void updateIntervals() {
        for (int i = 0; i < intervalKeys.length; i++) {
            String key = intervalKeys[i];
            booleanOptions.put(key, intervals.get(i).getEnabled());
            //System.out.println("Key: " + key);
        }
    }

    /*
    Method to send options data back to the previous activity.
     */
    private void sendDataBackToCallingActivity() {
        updateIntervals();

        Intent resultIntent = new Intent();

        //Put data in the intent
        resultIntent.putExtra("booleanHashmap", booleanOptions);
        resultIntent.putExtra("intervalDirection", intervalDirection);
        resultIntent.putExtra("numQuestions", questionCount);

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
        //save the list of items
        outState.putParcelableArrayList("Intervals List", intervals);
        //save question count
        outState.putInt("Question Count", questionCount);

        /*
        if (booleanOptions.get("Static Root") != null){
            //save the static root option
            outState.putBoolean("Static Root", Boolean.TRUE.equals(booleanOptions.get("Static Root")));
        }
        else {
            Log.d("ERROR", "booleanOptions.get(\"Static Root\") is null in onSaveInstanceState");
        }
        */

        outState.putString("Interval Direction", intervalDirection);

        //added these
        outState.putSerializable("booleanOptions", booleanOptions);

    }

    /*
    Method to restore data from onSaveInstanceState
     */
    /*
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        Log.d("Data Loading Test", "In Restore Instance State");
        //If the list was saved, retrieve it
        if (savedInstanceState.containsKey("Intervals List")) {
            intervals = savedInstanceState.getParcelableArrayList("Intervals List");
            //viewAdapter.notifyDataSetChanged();
        }
        else {
            Log.d("Data Loading Test", "Failed to load interval list");
        }
        if (savedInstanceState.containsKey("Question Count")) {
            questionCount = savedInstanceState.getInt("Question Count");
        }
        else {
            Log.d("Data Loading Test", "Failed to load question count");
        }
        if (savedInstanceState.containsKey("Static Root")) {
            booleanOptions.put("Static Root", savedInstanceState.getBoolean("Static Root"));
        }
        else {
            Log.d("Data Loading Test", "Failed to load static root");
        }
        if (intervalDirection.contains("Interval Direction")) {
            intervalDirection = savedInstanceState.getString("Interval Direction");
        }
        else {
            Log.d("Data Loading Test", "Failed to load interval direction");
        }

        //added this
        if (savedInstanceState.containsKey("booleanOptions")) {
            booleanOptions = (HashMap<String, Boolean>) savedInstanceState.getSerializable("booleanOptions");
        }
    }
    */

}