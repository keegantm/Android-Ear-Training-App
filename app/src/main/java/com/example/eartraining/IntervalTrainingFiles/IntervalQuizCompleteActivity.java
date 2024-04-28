package com.example.eartraining.IntervalTrainingFiles;

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

import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;


/*
Activity for after the interval quiz is over
Sends data to the DB and displays stats for the quiz
*/
public class IntervalQuizCompleteActivity extends AppCompatActivity {

    //intervals that could have been on the quiz
    private ArrayList<String> intervalChoices;
    //intervals the user identified correctly
    private ArrayList<String> correct;
    //intervals the user identified incorrectly
    private ArrayList<String> incorrect;

    //boolean for if the data has been sent to the DB
    private boolean sentData;

    private LinearLayout statsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_quiz_complete);

        statsLayout = findViewById(R.id.intervalQuizStatsLayout);
        Button doneButton = findViewById(R.id.doneButton);

        //Config change
        if (savedInstanceState != null) {
            Log.d("LIFE CYCLE", "Restoring Saved State");

            intervalChoices = savedInstanceState.getStringArrayList("intervalChoices");
            correct = savedInstanceState.getStringArrayList("correct");
            incorrect = savedInstanceState.getStringArrayList("incorrect");
            sentData = savedInstanceState.getBoolean("sendData");

        }
        //create with passed in data
        else {

            Log.d("LIFE CYCLE", "Creating new values");
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                // Extract data from the intent extras
                intervalChoices = extras.getStringArrayList("intervalChoices");
                correct = extras.getStringArrayList("correct");
                incorrect = extras.getStringArrayList("incorrect");
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
    Method to display the user's
    performance in the quiz
     */
    private void displayStats() {
        if (intervalChoices != null && correct != null && incorrect != null) {

            int oneDP = (int) getResources().getDisplayMetrics().density;
            int textColor = ContextCompat.getColor(this, R.color.text_and_border);

            // Iterate through the intervalChoices
            for (String interval : intervalChoices) {
                // Find how many times this interval was in the correct and incorrect arrays
                int correctCount = Collections.frequency(correct, interval);
                int incorrectCount = Collections.frequency(incorrect, interval);

                // Calculate the success ratio for this interval
                String ratio = correctCount + "/" + (correctCount + incorrectCount);

                // Create a TextView to display the success ratio
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, oneDP * 10); // Margin bottom of 10dp
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(COMPLEX_UNIT_SP, 20);
                textView.setPadding(0, 0, 0, 10); // Margin bottom of 10dp
                String formattedText = interval + " : " + ratio;
                textView.setText(formattedText);
                textView.setTextColor(textColor);
                // Add the TextView to the linear layout
                statsLayout.addView(textView);
            }
        }
    }

    /*
    Method to send data to the db about the user's
    correct and incorrect guesses
     */
    private void sendData() {
        //only send if the data hasn't been sent
        if (!sentData) {
            //DB thread
            Runnable runnable = () -> {
                try (DBHelper dbHelper = new DBHelper(this)) {
                    LocalDate currentDate = LocalDate.now();
                    // Use dbHelper object here
                    boolean success = dbHelper.addIntervalValues(correct, incorrect, currentDate);
                    Log.d("DB TEST", "Update interval values returned :" + success);
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
    Make the back button direct the user to the main menu
     */
    public void handleBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(IntervalQuizCompleteActivity.this, MainActivity.class);
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
        outState.putStringArrayList("intervalChoices", intervalChoices);
        outState.putStringArrayList("correct", correct);
        outState.putStringArrayList("incorrect", incorrect);
        outState.putBoolean("sentData", sentData);
    }


}