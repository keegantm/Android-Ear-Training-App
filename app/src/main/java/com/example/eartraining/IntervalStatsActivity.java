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

import java.util.HashMap;
import java.time.LocalDate;
import java.text.DecimalFormat;

/*
Activity to view the user's performance statistics for interval identification
 */
public class IntervalStatsActivity extends AppCompatActivity {

    //names of the intervals, keys for retrieving from the db
    private static final String[] intervalNames = {
            "Unison", "Minor Second", "Major Second", "Minor Third", "Major Third",
            "Perfect Fourth", "Tritone", "Perfect Fifth", "Minor Sixth", "Major Sixth",
            "Minor Seventh", "Major Seventh", "Octave", "Minor Ninth", "Major Ninth"
    };

    //where the stats will be put
    private LinearLayout dataLayout;
    //drop down to select timeframe
    private Spinner dropdownMenu;

    //class used to access DB
    private DBHelper dbHelper;
    //used to update UI after retrieving data from the db
    private Handler handler = new Handler();

    //global variables for the DB

    //Hashmaps that will contain every interval type, and how many total times the user
    //correctly/incorrectly identified that interval (within the selected time frame)
    private HashMap<String, Integer> correctIntervalGuesses;
    private HashMap<String, Integer> incorrectIntervalGuesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        dataLayout = findViewById(R.id.intervalQuizStatsLayout);
        dropdownMenu = findViewById(R.id.dropdown);

        //got data from DB. display the stats
        handler = new Handler(Looper.getMainLooper()) {
            @Override public void handleMessage(@NonNull Message msg) {
                //get the numbers to display
                int success = msg.arg1;

                Log.d("DB DEBUG", "Handler got : " + success);
                displayStats();

            }
        };

        //set up the dropdown menu
        String[] timeOptions = new String[]{"Last 30 Days", "Last 3 Months", "Last 6 Months"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timeOptions);
        dropdownMenu.setAdapter(adapter);

        dbHelper = new DBHelper(this);

        //listener for drop down menu. Gets the stats from the db for the specified time period.
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

        //restore the previous state if there was a config change
        if (savedInstanceState != null) {
            int selectedIndex = savedInstanceState.getInt("DropdownIndex", 0);
            dropdownMenu.setSelection(selectedIndex);
        }

        /* Mock data
        LocalDate dateOne = LocalDate.now().minusMonths(2);
        LocalDate dateTwo = LocalDate.now().minusMonths(4);
        dbHelper.addMockIntervalData(dateOne);
        dbHelper.addMockIntervalData(dateTwo);
         */

    }

    /*
    Method to display the stats. Creates text views and adds them to the linear layout.
     */
    private void displayStats() {
        if (correctIntervalGuesses != null && incorrectIntervalGuesses != null) {

            Log.d("DEBUG", "trying to create stats display");
            //clear the layout
            dataLayout.removeAllViews();

            int oneDP = (int) getResources().getDisplayMetrics().density;
            int textColor = ContextCompat.getColor(this, R.color.text_and_border);

            // Iterate through the intervals
            for (String interval : intervalNames) {

                // Find how many times this interval was in the correct and incorrect arrays
                int correctCount = correctIntervalGuesses.get(interval);
                int incorrectCount = incorrectIntervalGuesses.get(interval);

                int total = correctCount + incorrectCount;

                float intervalAccuracy;
                if (total == 0) {
                    intervalAccuracy = 0;
                }
                else {
                    intervalAccuracy = (float) correctCount /total;
                }

                DecimalFormat df = new DecimalFormat("#.##");
                String formattedAccuracy = df.format(intervalAccuracy * 100);

                String intervalStats = interval + " Accuracy : " + formattedAccuracy +"%\n" + "Total Attempts : " + total;

                // Create a TextView to display the success ratio
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, oneDP * 10);
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(COMPLEX_UNIT_SP, 20);
                textView.setPadding(0, 0, 0, 10);
                textView.setText(intervalStats);
                textView.setTextColor(textColor);

                // Add the TextView to the linear layout
                dataLayout.addView(textView);
            }
        }
        else {
            Log.d("Display Stats", "Failed to display stats");
        }
    }

    /*
    Method to retrieve user guess data from the database, after the LocalDate date.
     */
    private void getData(LocalDate date) {

        //on another thread
        Runnable runnable = () -> {

            correctIntervalGuesses = dbHelper.getIntervalCountsAfterDate(date, 1);
            incorrectIntervalGuesses = dbHelper.getIntervalCountsAfterDate(date, 0);


            //Log.d("DB DEBUG", "correct interval hashmap :" + correctIntervalGuesses.toString());
            //Log.d("DB DEBUG", "incorrect interval hashmap :" + incorrectIntervalGuesses.toString());


            //id sending an int isn't working, try using Message getData() and setData() to send whole hashmap
            if (correctIntervalGuesses != null && incorrectIntervalGuesses != null) {
                Log.d("DB DEBUG", "Hashmaps are not null");
                int messageSuccess = 1;
                handler.sendMessage(Message.obtain(handler, 1, messageSuccess));
            }
            else {
                Log.d("DB ERROR", "Error getting correct or incorrect interval guesses from hashmap");
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