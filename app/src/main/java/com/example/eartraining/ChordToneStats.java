package com.example.eartraining;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;

/*
Activity to view the user's performance statistics for chord tone identification
 */
public class ChordToneStats extends AppCompatActivity {

    //names of all major triad chord tones
    String[] majTriadChordToneKeys = {
            "Root", "3rd", "5th", "♭7th", "7th", "♭9th", "9th", "♯9th", "♯11th", "♭13th", "13th"
    };

    //names of all minor triad chord tones
    String[] minTriadChordToneKeys = {
            "Root", "♭3rd", "5th", "♭7th", "7th", "♭9th", "9th", "11th", "♯11th", "♭13th", "13th"
    };

    //where the stats are displayed
    private LinearLayout dataLayout;
    //dropdown for selecting time frame to get the stats by
    private Spinner dropdownMenu;

    private DBHelper dbHelper;

    //handler to update the UI thread with new data
    private Handler handler = new Handler();

    //number of times each major triad chord tone has been correctly identified
    private HashMap<String, Integer> correctMajorTriadChordToneGuesses;
    //number of times each major triad chord tone has been incorrectly identified
    private HashMap<String, Integer> incorrectMajorTriadChordToneGuesses;

    //number of times each minor triad chord tone has been correctly identified
    private HashMap<String, Integer> correctMinorTriadChordToneGuesses;
    //number of times each minor triad chord tone has been incorrectly identified
    private HashMap<String, Integer> incorrectMinorTriadChordToneGuesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_tone_stats);

        dataLayout = findViewById(R.id.chordToneQuizStatsLayout);
        dropdownMenu = findViewById(R.id.dropdown);

        //get data from DB and display it
        handler = new Handler(Looper.getMainLooper()) {
            @Override public void handleMessage(@NonNull Message msg) {
                //get the numbers to display
                int success = msg.arg1;

                Log.d("DB DEBUG", "Handler got : " + success);
                displayStats();
            }
        };

        //set up dropdown menu
        String[] timeOptions = new String[]{"Last 30 Days", "Last 3 Months", "Last 6 Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeOptions);
        dropdownMenu.setAdapter(adapter);

        dbHelper = new DBHelper(this);

        //on click listener for the dropdown, queries the db for stats by the user's selection
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("DROPDOWN", "position " + position + " selected");

                LocalDate paramDate = null;
                LocalDate currentDate = LocalDate.now();
                switch (position) {
                    case 0:
                        paramDate = currentDate.minusDays(30);
                        break;
                    case 1:
                        paramDate = currentDate.minusMonths(3);
                        break;
                    case 2:
                        paramDate = currentDate.minusMonths(6);
                        break;
                }


                Log.d("TEST", "dropdown, getting data");
                getData(paramDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dropdownMenu.setSelection(0);
            }
        });

        //preserve the dropdown selection from before config change
        if (savedInstanceState != null) {
            int selectedIndex = savedInstanceState.getInt("DropdownIndex", 0);
            dropdownMenu.setSelection(selectedIndex);
        }

        /* Mock data
        LocalDate dateOne = LocalDate.now().minusMonths(2);
        LocalDate dateTwo = LocalDate.now().minusMonths(4);
        dbHelper.addMockChordToneData(dateOne);
        dbHelper.addMockChordToneData(dateTwo);
        */

    }

    /*
    Method to display the stats. Creates text views and adds them to the linear layout.
     */
    private void displayStats() {
        if (correctMajorTriadChordToneGuesses != null && incorrectMajorTriadChordToneGuesses != null && correctMinorTriadChordToneGuesses != null && incorrectMinorTriadChordToneGuesses != null) {

            Log.d("DEBUG", "trying to create stats display");
            //clear the layout
            dataLayout.removeAllViews();

            createTextView(true, "Major Triad Chord Tone Stats :");

            //iterate through the major triad keys
            for (String chordTone : majTriadChordToneKeys) {
                int correctCount = correctMajorTriadChordToneGuesses.get(chordTone);
                int incorrectCount = incorrectMajorTriadChordToneGuesses.get(chordTone);

                int total = correctCount + incorrectCount;

                float chordToneAccuracy;
                if (total == 0) {
                    chordToneAccuracy = 0;
                }
                else {
                    chordToneAccuracy = (float) correctCount /total;
                }

                DecimalFormat df = new DecimalFormat("#.##");
                String formattedAccuracy = df.format(chordToneAccuracy * 100);

                String formattedStatsString = chordTone + " Accuracy : " + formattedAccuracy +"%\n" + "Total Attempts : " + total;

                createTextView(false, formattedStatsString);
            }

            createTextView(true, "Minor Triad Chord Tone Stats :");

            //iterate through the major triad keys
            for (String chordTone : minTriadChordToneKeys) {
                int correctCount = correctMinorTriadChordToneGuesses.get(chordTone);
                int incorrectCount = incorrectMinorTriadChordToneGuesses.get(chordTone);

                int total = correctCount + incorrectCount;

                float chordToneAccuracy;
                if (total == 0) {
                    chordToneAccuracy = 0;
                }
                else {
                    chordToneAccuracy = (float) correctCount /total;
                }

                DecimalFormat df = new DecimalFormat("#.##");
                String formattedAccuracy = df.format(chordToneAccuracy * 100);

                String formattedStatsString = chordTone + " Accuracy : " + formattedAccuracy +"%\n" + "Total Attempts : " + total;

                createTextView(false, formattedStatsString);
            }
        }
        else {
            Log.d("Display Stats", "Failed to display stats");
        }
    }

    /*
    Helper method to create text views for the linear layout
     */
    private void createTextView(Boolean header, String formattedString) {
        int oneDP = (int) getResources().getDisplayMetrics().density;
        int textColor = ContextCompat.getColor(this, R.color.text_and_border);

        //headers have different look than text views containing stat information.
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if (header) {
            // Create a TextView to display the success ratio
            params.setMargins(0, oneDP * 10, 0, oneDP * 10);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(COMPLEX_UNIT_SP, 25);
        }
        else {
            // Create a TextView to display the success ratio
            params.setMargins(0, 0, 0, oneDP * 10);
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(COMPLEX_UNIT_SP, 20);
        }
        textView.setPadding(0, 0, 0, 10);
        textView.setText(formattedString);
        textView.setTextColor(textColor);
        dataLayout.addView(textView);
    }

    /*
    Method to retrieve user guess data from the database, after the LocalDate date.
    */
    private void getData(LocalDate date) {

        //on another thread
        Runnable runnable = () -> {

            correctMajorTriadChordToneGuesses = dbHelper.getChordToneCountsAfterDate(date, 1, "Major Triad");
            incorrectMajorTriadChordToneGuesses = dbHelper.getChordToneCountsAfterDate(date, 0, "Major Triad");

            correctMinorTriadChordToneGuesses = dbHelper.getChordToneCountsAfterDate(date, 1, "Minor Triad");
            incorrectMinorTriadChordToneGuesses = dbHelper.getChordToneCountsAfterDate(date, 0, "Minor Triad");

            //if sending an int isn't working, try using Message getData() and setData() to send whole hashmap
            if (correctMajorTriadChordToneGuesses != null && incorrectMajorTriadChordToneGuesses != null && correctMinorTriadChordToneGuesses != null && incorrectMinorTriadChordToneGuesses != null) {
                Log.d("DB DEBUG", "Hashmaps are not null");
                int messageSuccess = 1;
                handler.sendMessage(Message.obtain(handler, 1, messageSuccess));
            }
            else {
                Log.d("DB ERROR", "Error getting correct or incorrect chord tone guesses from hashmap");
                handler.sendMessage(Message.obtain(handler, 1, 0));
            }

        };
        //start the thread
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Dropdown Index", dropdownMenu.getSelectedItemPosition());

    }
}