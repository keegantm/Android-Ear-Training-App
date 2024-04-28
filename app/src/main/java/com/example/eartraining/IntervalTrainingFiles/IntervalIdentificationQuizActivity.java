package com.example.eartraining.IntervalTrainingFiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.GridLayout;             //grid layout not in xml
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout; //grid layout in xml

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eartraining.ButtonStateListener;
import com.example.eartraining.CorrectFragment;
import com.example.eartraining.IncorrectFragment;
import com.example.eartraining.R;

import java.util.ArrayList;
import java.util.HashMap;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/*
Activity for the interval identification quiz.

User can play the audio of an interval. They then select a guess.
A Fragment appears, dependent on if their guess was correct
 */
public class IntervalIdentificationQuizActivity extends AppCompatActivity implements ButtonStateListener {

    //total number of questions in the quiz
    private int numQuestions;
    //if all the intervals are starting on the same note
    private boolean staticRoot;
    //ascending, descending, or a mix
    private String intervalDirection;
    //which intervals are available for the activity
    private ArrayList<String> intervalChoices;

    //quiz object to contain quiz logic
    private IntervalQuiz quiz;
    //what question the user is on
    private int questionNumber;

    //filled with the names of intervals the user correctly identified
    private ArrayList<String> correct;
    //filled with the names of intervals the user incorrectly identified
    private ArrayList<String> incorrect;

    //text stating question number
    private TextView titleText;
    //button that plays an interval
    private Button intervalButton;

    //Pure data audio patch file
    private File patchFile;

    //hashmap of number of midi note ints (half steps) to move from a starting note to create each interval
    Map<String, Integer> intervalMath = new HashMap<String, Integer>(){{
        put("Unison", 0);
        put("Minor Second", 1);
        put("Major Second", 2);
        put("Minor Third", 3);
        put("Major Third", 4);
        put("Perfect Fourth", 5);
        put("Tritone", 6);
        put("Perfect Fifth", 7);
        put("Minor Sixth", 8);
        put("Major Sixth", 9);
        put("Minor Seventh", 10);
        put("Major Seventh", 11);
        put("Octave", 12);
        put("Minor Ninth", 13);
        put("Major Ninth", 14);
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_identification_quiz);

        titleText = findViewById(R.id.IntervalQuestionNum);
        intervalButton = findViewById(R.id.playIntervalButton);


        // Restore instance state if available
        if (savedInstanceState != null) {
            numQuestions = savedInstanceState.getInt("numQuestions");
            questionNumber = savedInstanceState.getInt("questionNumber");

            correct = savedInstanceState.getStringArrayList("correct");
            incorrect = savedInstanceState.getStringArrayList("incorrect");

            intervalChoices = savedInstanceState.getStringArrayList("intervalChoices");

            quiz = savedInstanceState.getParcelable("quiz");
        }
        //this is the first time creating the activity
        else {

            //get data that was passed in and set up the quiz object & params
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                // Extract data from the intent extras
                intervalDirection = extras.getString("intervalDirection");
                numQuestions = extras.getInt("numQuestions");
                staticRoot = extras.getBoolean("staticRoot");
                intervalChoices = extras.getStringArrayList("selectedIntervals");

                setUpNewQuiz();
            }
        }

        // Set up PureData audio resources.
        try {
            Log.d("PATCH", "Initializing Patch");
            initPd();
            Log.d("PATCH", "Loading Patch");
            loadPatch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //create answer buttons and assign listeners to them
        setUpAnswerButtons();

        //set up the interval audio for the current question
        setUpQuestion();

        //set up listener to the play interval button, so it plays an interval
        intervalButton.setOnClickListener(v -> {
            try {
                //tells the audio patch to create audio
                PdBase.sendBang("trigger");
            } catch (Exception e) {
                Log.d("INTERVAL BUTTON ERROR", e.toString());
                throw new RuntimeException(e);
            }
        });
    }

    /*
    Method to initialize the Pure Data audio thread
     */
    private void initPd() throws IOException {
        //configure the audio glue
        int sampleRate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(sampleRate, 0, 2, 8, true);
    }

    /*
    Method to load the patch onto the audio thread
     */
    private void loadPatch() throws IOException {
        File dir = getFilesDir();
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.intervalversiontwo), dir, true);

//        File[] files = dir.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                Log.d("File in directory", file.getName());
//            }
//        }

        patchFile = new File(dir, "intervalversiontwo.pd");
        //Log.d("DEBUG PATCH", String.valueOf(patchFile.exists()));

        //IoUtils.extractZipResource(getResources().openRawResource(R.raw.interval), dir, true);
        //patchFile = new File(dir, "interval.pd");



        PdBase.openPatch(patchFile.getAbsolutePath());
    }


    /*
    Method to initialize a new quiz and quiz parameters
     */
    private void setUpNewQuiz() {
        quiz = new IntervalQuiz(numQuestions,intervalDirection,staticRoot,intervalChoices);
        questionNumber = 0;
        correct = new ArrayList<String>();
        incorrect = new ArrayList<String>();

        quiz.logQuizParameters();
    }

    /*
    Method to create buttons for each possible interval.
    Assigns listeners to them to check if they are the correct answer
     */
    @SuppressLint("ResourceAsColor")
    private void setUpAnswerButtons() {
        //get the grid layout that contains all the buttons
        GridLayout grid = findViewById(R.id.grid);

        //height of buttons
        int oneDP = (int) getResources().getDisplayMetrics().density;
        //create button for each interval choice
        for (String interval : intervalChoices) {
            //create a new button
            Button newButton = new Button(this);
            newButton.setText(interval);

            //button height and width
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = oneDP * 100;

            //                app:layout_gravity="fill"
            //                app:layout_columnWeight="1"
            layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f); // Fills the available vertical space
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f); // Fills the available horizontal space

            //button margins
            layoutParams.setMargins(oneDP * 10, oneDP * 10, oneDP * 10, oneDP * 10);

            // Set corner radius for the button
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(R.color.light_blue);
            shape.setCornerRadius(oneDP * 10); // Set corner radius to 10dp

            int buttonBackgroundColor = ContextCompat.getColor(this, R.color.button_blue);
            int buttonTextColor = ContextCompat.getColor(this, R.color.text_and_border);

            //add the button to the grid view
            newButton.setBackground(shape);
            newButton.setBackgroundColor(buttonBackgroundColor);
            newButton.setTextColor(buttonTextColor);
            newButton.setLayoutParams(layoutParams);
            grid.addView(newButton, layoutParams);

            //see if this button was the right answer
            newButton.setOnClickListener(v -> checkAnswer(interval));
        }

    }

    /*
    Method to send the notes of the interval to the Pure Data patch,
    and to update the question number
     */
    private void setUpQuestion() {
        Log.d("DEBUG", "Setting up question with quiz index :" + questionNumber);

        if (questionNumber >= numQuestions) {
            return;
        }

        //set up the interval audio for this question
        Interval thisInterval = quiz.getQuizInterval(questionNumber);

        //Log.d("PATCH", "Sending Midi Notes");
        //Log.d("PATCH", "Audio Running? :" +  PdAudio.isRunning());

        //update the patch first and second note
        PdBase.sendFloat("firstMidiNote", thisInterval.getFirstNote());
        PdBase.sendFloat("secondMidiNote", thisInterval.getSecondNote());

        //questionNumber is used as an array index
        int formattedQuestionNum = questionNumber + 1;

        //update the title question number
        String finishedText = "Question " + formattedQuestionNum + " of " + numQuestions;
        titleText.setText(finishedText);

    }

    /*
    Method that checks the users answer, then updates the question number
    and audio of the interval button
     */
    private void checkAnswer(String userAnswer) {
        boolean userCorrect = false;

        //check if the user's answer was correct
        if (quiz.checkAnswer(userAnswer, questionNumber)) {
            userCorrect = true;
            //add string to correct
            correct.add(userAnswer);
            Log.d("DEBUG", "Added to correct :" + userAnswer);
        }
        else {
            Interval correctInterval = quiz.getQuizInterval(questionNumber);
            incorrect.add(correctInterval.getName());
            Log.d("DEBUG", "Added to incorrect :" + correctInterval.getName());

        }

        //get the quiz's interval object
        Interval correctInterval = quiz.getQuizInterval(questionNumber);

        //create interval representing the user's choice
        int  userSecondNote = 0;
        if (intervalMath.get(userAnswer) != null) {
            int userSecondNoteDistance = intervalMath.get(userAnswer);

            //unison or ascending interval
            if (correctInterval.getFirstNote() <= correctInterval.getSecondNote()){
                userSecondNote = correctInterval.getFirstNote() + userSecondNoteDistance;
            }
            //descending interval
            else {
                userSecondNote = correctInterval.getFirstNote() - userSecondNoteDistance;
            }

        }
        else {
            Log.d("ERROR", "intervalMath.get(userAnswer) produced a null pointer");
        }

        //create an interval representing the user's choice
        Interval userInterval = new Interval(correctInterval.getFirstNote(), userSecondNote, userAnswer);

        //Open a fragment notifying the user if they were right
        openCorrectnessFragment(userCorrect, correctInterval, userInterval);

        //next question
        questionNumber += 1;

        //set up the audio and quiz params for the next question
        setUpQuestion();

    }

    /*
    Method to open a fragment after the user makes a guess.
    Either opens a correct fragment, or an incorrect fragment
     */
    private void openCorrectnessFragment(boolean correct, Interval correctInterval, Interval incorrectInterval ) {
        disableButtons();

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragManager.beginTransaction();

        if (correct) {
            // Create a fragment for correct answer
            CorrectFragment correctFragment = new CorrectFragment();
            transaction.replace(R.id.quizOverallLayout, correctFragment); // Replace fragment_container with the ID of your container layout
        } else {

            // Create a fragment for incorrect answer
            IncorrectFragment incorrectFragment = new IncorrectFragment();

            Bundle args = new Bundle();
            // Indicate this is for an interval
            args.putString("Incorrect Type", "Interval");

            // Put info about the correct interval
            args.putString("Correct String", correctInterval.getName());

            // Put info about the incorrect interval
            args.putString("Incorrect String", incorrectInterval.getName());

            incorrectFragment.setArguments(args);

            transaction.replace(R.id.quizOverallLayout, incorrectFragment);

        }

        //transaction.addToBackStack(null); // Add the transaction to the back stack
        transaction.commit();
    }

    /*
    Method to re-enable the activity buttons when the user returns from a fragment
     */
    public void enableButtons(boolean c){

        //check if the quiz is over
        if (questionNumber == numQuestions) {

            Log.d("DEBUG", "sending correct data :" + correct);
            Log.d("DEBUG", "sending incorrect data :" + incorrect);


            Intent intent = new Intent(this, IntervalQuizCompleteActivity.class);
            intent.putExtra("intervalChoices", intervalChoices);
            intent.putExtra("correct", correct);
            intent.putExtra("incorrect", incorrect);

            startActivity(intent);
            finish(); //don't return to here with the back button
        }


        // Enable buttons in the GridLayout
        GridLayout gridLayout = findViewById(R.id.grid);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                child.setEnabled(true);
            }
        }

        // Enable intervalButton
        intervalButton.setEnabled(true);

    }

    /*
    Method to disable the buttons of this activity when the user
    opens a fragment
     */
    public void disableButtons() {

        // Disable buttons in the GridLayout
        GridLayout gridLayout = findViewById(R.id.grid);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }

        // Disable intervalButton
        intervalButton.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //pause audio thread
        PdAudio.startAudio(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //resume audio thread
        PdAudio.stopAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //destroy patch and audio thread
        PdAudio.release();
        PdBase.release();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("numQuestions", numQuestions);
        outState.putInt("questionNumber", questionNumber);

        outState.putStringArrayList("correct", correct);
        outState.putStringArrayList("incorrect", incorrect);
        outState.putStringArrayList("intervalChoices", intervalChoices);
        outState.putParcelable("quiz", quiz);
    }

     
}