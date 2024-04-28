package com.example.eartraining.ChordToneTrainingFiles;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eartraining.DBHelper;
import com.example.eartraining.MainActivity;
import com.example.eartraining.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/*
Activity for after the chord tone quiz is over
Sends data to the DB and displays stats for the quiz
*/
public class ChordToneQuizCompleteActivity extends AppCompatActivity {
    //major chord tones that could have been on the quiz
    private ArrayList<String> majorTriadNoteChoices;
    //minor chord tones that could have been on the quiz
    private ArrayList<String> minorTriadNoteChoices;

    //major chord tones the user correctly identified
    private ArrayList<String> majorTriadCorrect;
    //major chord tones the user incorrectly identified
    private ArrayList<String> majorTriadIncorrect;

    //minor chord tones the user correctly identified
    private ArrayList<String> minorTriadCorrect;
    //minor chord tones the user incorrectly identified
    private ArrayList<String> minorTriadIncorrect;


    //boolean for if the data has been sent to the DB
    private boolean sentData;

    private LinearLayout statsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_tone_quiz_complete);

        statsLayout = findViewById(R.id.chordToneQuizStatsLayout);
        Button doneButton = findViewById(R.id.doneButton);

        //Config change
        if (savedInstanceState != null) {
            Log.d("LIFE CYCLE", "Restoring Saved State");

            majorTriadNoteChoices = savedInstanceState.getStringArrayList("majorTriadNoteChoices");
            minorTriadNoteChoices = savedInstanceState.getStringArrayList("minorTriadNoteChoices");
            majorTriadCorrect = savedInstanceState.getStringArrayList("majorTriadCorrect");
            majorTriadIncorrect = savedInstanceState.getStringArrayList("majorTriadIncorrect");
            minorTriadCorrect = savedInstanceState.getStringArrayList("minorTriadCorrect");
            minorTriadIncorrect = savedInstanceState.getStringArrayList("minorTriadIncorrect");

            sentData = savedInstanceState.getBoolean("sendData");

        }
        //create with passed in data
        else {

            Log.d("LIFE CYCLE", "Creating new values");
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                // Extract data from the intent extras
                majorTriadNoteChoices = extras.getStringArrayList("majorTriadChordToneOptions");
                minorTriadNoteChoices = extras.getStringArrayList("minorTriadChordToneOptions");
                majorTriadCorrect = extras.getStringArrayList("majorTriadCorrect");
                majorTriadIncorrect = extras.getStringArrayList("majorTriadIncorrect");
                minorTriadCorrect = extras.getStringArrayList("minorTriadCorrect");
                minorTriadIncorrect = extras.getStringArrayList("minorTriadIncorrect");

                sentData = false;
            }
        }

        //send data to the DB
        sendData();
        //display performance on this quiz
        displayStats();


        doneButton.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        //back button should go to main menu
        handleBackButton();

    }

    /*
    Method to send data to the db about the user's
    correct and incorrect guesses
     */
    public void sendData() {
        if (!sentData) {
            //DB thread
            Runnable runnable = () -> {
                try (DBHelper dbHelper = new DBHelper(this)) {
                    LocalDate currentDate = LocalDate.now();
                    // Use dbHelper object here
                    boolean success = dbHelper.addChordToneValues(majorTriadCorrect, majorTriadIncorrect, minorTriadCorrect, minorTriadIncorrect, currentDate);
                    Log.d("DB TEST", "Add chord tone values returned :" + success);
                    sentData = success;
                } catch (Exception e) {
                    Log.d("DATABASE ERROR", e.toString());
                }
            };

            Thread myThread = new Thread(runnable);
            myThread.start();
        }
    }

    /*
    Method to display the user's
    performance in the quiz
     */
    public void displayStats() {
        int oneDP = (int) getResources().getDisplayMetrics().density;
        int textColor = ContextCompat.getColor(this, R.color.text_and_border);

        if (majorTriadNoteChoices != null) {
            //create title

            // Create a TextView to display the success ratio
            TextView majTitleTV = new TextView(this);
            LinearLayout.LayoutParams majTriadTitleLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            majTriadTitleLayoutParams.setMargins(0, oneDP * 20, 0, oneDP * 20);
            majTitleTV.setLayoutParams(majTriadTitleLayoutParams);
            majTitleTV.setGravity(Gravity.CENTER);
            majTitleTV.setTextSize(COMPLEX_UNIT_SP, 25);
            majTitleTV.setPadding(0, 0, 0, 10);
            String majTitle = "Major Triad Stats :";
            majTitleTV.setText(majTitle);
            majTitleTV.setTextColor(textColor);
            // Add the TextView to the linear layout
            statsLayout.addView(majTitleTV);

            // Iterate through the chord tones
            for (String majorTriadOption : majorTriadNoteChoices) {
                // Find how many times this chord tone was in the correct and incorrect arrays
                int correctCount = Collections.frequency(majorTriadCorrect, majorTriadOption);
                int incorrectCount = Collections.frequency(majorTriadIncorrect, majorTriadOption);

                // Calculate the success ratio for this chord tone
                String ratio = correctCount + "/" + (correctCount + incorrectCount);

                // Create a TextView to display the success ratio
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams majTriadStatParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                majTriadStatParams.setMargins(0, 0, 0, oneDP * 10); // Margin bottom of 10dp
                textView.setLayoutParams(majTriadStatParams);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(COMPLEX_UNIT_SP, 20);
                textView.setPadding(0, 0, 0, 10);
                String formattedText = majorTriadOption + " : " + ratio;
                textView.setText(formattedText);
                textView.setTextColor(textColor);

                // Add the TextView to the linear layout
                statsLayout.addView(textView);
            }
        }

        if (minorTriadNoteChoices != null) {
            //create title

            // Create a TextView to display the success ratio
            TextView minorTitleTV = new TextView(this);
            LinearLayout.LayoutParams minTriadTitleLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            minTriadTitleLayoutParams.setMargins(0, oneDP * 20, 0, oneDP * 20);
            minorTitleTV.setLayoutParams(minTriadTitleLayoutParams);
            minorTitleTV.setGravity(Gravity.CENTER);
            minorTitleTV.setTextSize(COMPLEX_UNIT_SP, 25);
            minorTitleTV.setPadding(0, 0, 0, 10);
            String majTitle = "Minor Triad Stats :";
            minorTitleTV.setText(majTitle);
            minorTitleTV.setTextColor(textColor);

            // Add the TextView to the linear layout
            statsLayout.addView(minorTitleTV);

            for (String minorTriadOption : minorTriadNoteChoices) {
                // Find how many times this chord tone was in the correct and incorrect arrays
                int correctCount = Collections.frequency(minorTriadCorrect, minorTriadOption);
                int incorrectCount = Collections.frequency(minorTriadIncorrect, minorTriadOption);

                // Calculate the success ratio for this chord tone
                String ratio = correctCount + "/" + (correctCount + incorrectCount);

                // Create a TextView to display the success ratio
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams minTriadStatParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                minTriadStatParams.setMargins(0, 0, 0, oneDP * 10); // Margin bottom of 10dp
                textView.setLayoutParams(minTriadStatParams);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(COMPLEX_UNIT_SP, 20);
                textView.setPadding(0, 0, 0, 10);
                String formattedText = minorTriadOption + " : " + ratio;
                textView.setText(formattedText);
                textView.setTextColor(textColor);

                // Add the TextView to the linear layout
                statsLayout.addView(textView);
            }
        }
    }

    /*
    Make the back button direct the user to the main menu
     */
    public void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChordToneQuizCompleteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        //callback to the back button event
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("majorTriadNoteChoices", majorTriadNoteChoices);
        outState.putStringArrayList("minorTriadNoteChoices",minorTriadNoteChoices);
        outState.putStringArrayList("majorTriadCorrect", majorTriadCorrect);
        outState.putStringArrayList("majorTriadIncorrect", majorTriadIncorrect);
        outState.putStringArrayList("minorTriadCorrect", minorTriadCorrect);
        outState.putStringArrayList("minorTriadIncorrect", minorTriadIncorrect);

        outState.putBoolean("sentData", sentData);

    }
}