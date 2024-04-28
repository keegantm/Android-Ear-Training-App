package com.example.eartraining.ChordToneTrainingFiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.gridlayout.widget.GridLayout;

import com.example.eartraining.ButtonStateListener;
import com.example.eartraining.CorrectFragment;
import com.example.eartraining.IncorrectFragment;
import com.example.eartraining.R;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/*
Activity for the chord tone identification quiz.

User can play the audio of an interval. They then select a guess.
A Fragment appears, dependent on if their guess was correct
 */
public class ChordToneIdentificationQuizActivity extends AppCompatActivity implements ButtonStateListener {
    //number of questions
    private int numQuestions;
    //if all the intervals are starting on the same note
    private boolean staticRoot;
    //major triad chord tone options
    ArrayList<String> majorTriadNoteChoices;
    //minor triad chord tone options
    ArrayList<String> minorTriadNoteChoices;
    //chords in the quiz
    HashMap<String, Boolean> chords;

    //quiz
    ChordToneQuiz quiz;

    //buttons for grid view
    ArrayList<Button> majTriadButtons;
    ArrayList<Button> minorTriadButtons;

    //what question the user is on
    private int questionNumber;

    //Chord Tones of the major triad that were correctly and incorrectly identified by the user
    private ArrayList<String> majorTriadCorrect;
    private ArrayList<String> majorTriadIncorrect;

    //Chord Tones of the minor triad that were correctly and incorrectly identified by the user
    private ArrayList<String> minorTriadCorrect;
    private ArrayList<String> minorTriadIncorrect;


    //text stating question number
    private TextView titleText;

    private GridLayout grid;

    //button that plays chord-tone audio
    private Button audioButton;

    //Pure data audio patch file
    private File patchFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_tone_identification_quiz);

        audioButton = findViewById(R.id.playChordToneButton);
        titleText = findViewById(R.id.chordToneQuestionNum);
        grid = findViewById(R.id.grid);

        // Restore instance state if available
        if (savedInstanceState != null) {
            numQuestions = savedInstanceState.getInt("numQuestions");
            questionNumber = savedInstanceState.getInt("questionNumber");

            majorTriadCorrect = savedInstanceState.getStringArrayList("majorTriadCorrect");
            majorTriadIncorrect = savedInstanceState.getStringArrayList("majorTriadIncorrect");

            minorTriadCorrect = savedInstanceState.getStringArrayList("minorTriadCorrect");
            minorTriadIncorrect = savedInstanceState.getStringArrayList("minorTriadIncorrect");

            majorTriadNoteChoices = savedInstanceState.getStringArrayList("majorTriadNoteChoices");
            minorTriadNoteChoices = savedInstanceState.getStringArrayList("minorTriadNoteChoices");

            quiz = savedInstanceState.getParcelable("quiz");

        }
        //this is the first time creating the activity
        else {

            //get data that was passed in and set up the quiz object & params
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                // Extract data from the intent extras
                numQuestions = extras.getInt("numQuestions");
                staticRoot = extras.getBoolean("staticRoot");
                chords = (HashMap<String, Boolean>) getIntent().getSerializableExtra("chords");

                if (chords.get("Major Triad")) {
                    majorTriadNoteChoices = extras.getStringArrayList("Major Triad Chord Tones");
                }
                if (chords.get("Minor Triad")) {
                    minorTriadNoteChoices = extras.getStringArrayList("Minor Triad Chord Tones");
                }

                setUpNewQuiz();
            }

        }

        majTriadButtons = new ArrayList<Button>();
        minorTriadButtons = new ArrayList<Button>();
        initializeButtonsForGridView();

        // Set up PureData audio resources.
        try {
            Log.d("PATCH", "Initializing Patch");
            initPd();
            Log.d("PATCH", "Loading Patch");
            loadPatch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setUpQuestion();

        //set up listener to the play audio button, so it plays a chord and the chord tone
        audioButton.setOnClickListener(v -> {
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
        IoUtils.extractZipResource(getResources().openRawResource(R.raw.triadchordtoneversiontwo), dir, true);

        patchFile = new File(dir, "triadchordtoneversiontwo.pd");

        PdBase.openPatch(patchFile.getAbsolutePath());
    }

    /*
    Creates buttons for each available chord tone.
     */
    public void initializeButtonsForGridView(){

        Log.d("DEBUG", "Creating buttons");
        //lets just create the buttons each time this is run
        if (majorTriadNoteChoices != null) {
            Log.d("DEBUG", "Major triads enabled.");

            //iterate through the array list of chord tone choices and create buttons for them
            for (String chordTone : majorTriadNoteChoices) {
                Log.d("DEBUG", "Major tone :" + chordTone);

                Button newButton = new Button(this);
                newButton.setText(chordTone);
                newButton.setOnClickListener(v -> checkAnswer(chordTone, "Major Triad"));

                majTriadButtons.add(newButton);
            }
        }
        else {
            Log.d("TEST", "majorTriadNoteChoices is null");
        }

        if (minorTriadNoteChoices != null) {
            Log.d("DEBUG", "Minor triads enabled.");

            for (String chordTone : minorTriadNoteChoices) {
                Log.d("DEBUG", "Minor tone :" + chordTone);

                Button newButton = new Button(this);
                newButton.setText(chordTone);
                newButton.setOnClickListener(v -> checkAnswer(chordTone, "Minor Triad"));

                minorTriadButtons.add(newButton);

            }
        }
        else {
            Log.d("TEST", "minorTriadNoteChoices is null");
        }
    }

    /*
    Method to clear the grid view, then insert an array of buttons into the grid view.
    Applies styling to the buttons
     */
    @SuppressLint("ResourceAsColor")
    public void populateGridView(ArrayList<Button> buttons) {
        Log.d("DEBUG", "button array size :" + buttons.size());

        //remove all buttons
        grid.removeAllViews();

        //height of buttons
        int oneDP = (int) getResources().getDisplayMetrics().density;
        //create button for each interval choice
        for (Button button : buttons) {

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
            shape.setCornerRadius(oneDP * 10);


            int buttonBackgroundColor = ContextCompat.getColor(this, R.color.button_blue);
            int buttonTextColor = ContextCompat.getColor(this, R.color.text_and_border);

            //add the button to the grid view
            button.setBackground(shape);
            button.setBackgroundColor(buttonBackgroundColor);
            button.setTextColor(buttonTextColor);
            button.setLayoutParams(layoutParams);
            grid.addView(button, layoutParams);

        }
    }

    /*
    Method to initialize data structues and variables for the quiz
     */
    public void setUpNewQuiz() {
        quiz = new ChordToneQuiz(numQuestions, staticRoot, chords, majorTriadNoteChoices, minorTriadNoteChoices);

        questionNumber = 0;
        majorTriadCorrect = new ArrayList<String>();
        majorTriadIncorrect = new ArrayList<String>();
        minorTriadCorrect = new ArrayList<String>();
        minorTriadIncorrect = new ArrayList<String>();

    }

    /*
    Method to send the notes of the chord + chord tone to the Pure Data patch,
    and to update the question number
     */
    private void setUpQuestion() {

        if (questionNumber >= numQuestions) {
            return;
        }

        //set up the interval audio for this question
        ChordTone thisChordTone = quiz.getQuizChordTone(questionNumber);

        int firstNote = thisChordTone.getFirstNote();
        int secondNote = thisChordTone.getSecondNote();
        int thirdNote = thisChordTone.getThirdNote();
        int chordToneNote = thisChordTone.getChordToneInt();

        //send the notes to the audio patch
        PdBase.sendList("triadNotes", firstNote, secondNote, thirdNote, chordToneNote);

        //questionNumber is used as an array index
        int formattedQuestionNum = questionNumber + 1;
        //update the title question number
        String finishedText = "Question " + formattedQuestionNum + " of " + numQuestions;
        titleText.setText(finishedText);

        //update button text
        String chordType = thisChordTone.getChordType();
        String audioBtnText = "Play a " + chordType + " & a Chord Tone";
        audioButton.setText(audioBtnText);

        if (Objects.equals(thisChordTone.getChordType(), "Major Triad")) {
            Log.d("DEBUG", "Passing in Major Buttons. Size is :" + majTriadButtons.size());
            populateGridView(majTriadButtons);
        }
        else {
            Log.d("DEBUG", "Passing in Minor Buttons. Size is :" + minorTriadButtons.size());
            populateGridView(minorTriadButtons);
        }
    }

    /*
    Method to check if the user's answer was correct.
    String chordTone - chord tone the user selected
    String chordType - the chord of the chord tone
     */
    public void checkAnswer(String chordTone, String chordType) {
        boolean userCorrect = false;

        //check if the user's answer was correct
        if (quiz.checkAnswer(chordTone, chordType, questionNumber)) {
            userCorrect = true;

            if (Objects.equals(chordType, "Major Triad")) {
                //add string to correct
                majorTriadCorrect.add(chordTone);
            } else if (Objects.equals(chordType, "Minor Triad")) {
                //add string to correct
                minorTriadCorrect.add(chordTone);
            }
        }
        else {

            ChordTone correctChordTone = quiz.getQuizChordTone(questionNumber);
            if (Objects.equals(chordType, "Major Triad")) {
                //add string to correct
                majorTriadIncorrect.add(correctChordTone.getChordToneName());
            } else if (Objects.equals(chordType, "Minor Triad")) {
                //add string to correct
                minorTriadIncorrect.add(correctChordTone.getChordToneName());
            }
        }

        //get the quiz's interval object
        ChordTone correctChordTone = quiz.getQuizChordTone(questionNumber);

        //Open a fragment notifying the user if they were right
        openCorrectnessFragment(userCorrect, correctChordTone, chordTone);

        //next question
        questionNumber += 1;

        //set up the audio and quiz params for the next question
        setUpQuestion();
    }

    /*
    Method to open a fragment, displaying if the user
    was correct or incorrect.
    boolean correct - was the user correct
    ChordTone correctChordTone - correct ChordTone of the question
    String userChordTone - name of the chord tone that the user guessed
     */
    private void openCorrectnessFragment(boolean correct, ChordTone correctChordTone, String userChordTone){
        disableButtons();

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragManager.beginTransaction();

        if (correct) {
            // Create a fragment for correct answer
            CorrectFragment correctFragment = new CorrectFragment();
            transaction.replace(R.id.quizOverallLayout, correctFragment);
        } else {

            // Create a fragment for incorrect answer
            IncorrectFragment incorrectFragment = new IncorrectFragment();

            Bundle args = new Bundle();
            // Indicate this is for an interval
            args.putString("Incorrect Type", "ChordTone");

            // Put info about the correct interval
            args.putString("Correct String", correctChordTone.getChordToneName());

            // Put info about the incorrect interval
            args.putString("Incorrect String", userChordTone);

            incorrectFragment.setArguments(args);

            transaction.replace(R.id.quizOverallLayout, incorrectFragment);

        }

        //transaction.addToBackStack(null); // Add the transaction to the back stack
        transaction.commit();
    }


    /*
    Method to re-enable buttons in the activity
     */
    @Override
    public void enableButtons(boolean remakePd) {
        //check if the quiz is over
        if (questionNumber == numQuestions) {
            Intent intent = new Intent(this, ChordToneQuizCompleteActivity.class);
            intent.putExtra("majorTriadChordToneOptions", majorTriadNoteChoices);
            intent.putExtra("minorTriadChordToneOptions", minorTriadNoteChoices);

            intent.putExtra("majorTriadCorrect", majorTriadCorrect);
            intent.putExtra("majorTriadIncorrect", majorTriadIncorrect);

            intent.putExtra("minorTriadCorrect", minorTriadCorrect);
            intent.putExtra("minorTriadIncorrect", minorTriadIncorrect);

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
        audioButton.setEnabled(true);
    }

    /*
    Method to disable buttons in the grid view
     */
    @Override
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
        audioButton.setEnabled(false);

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

        outState.putStringArrayList("majorTriadCorrect", majorTriadCorrect);
        outState.putStringArrayList("majorTriadIncorrect", majorTriadIncorrect);

        outState.putStringArrayList("minorTriadCorrect", minorTriadCorrect);
        outState.putStringArrayList("minorTriadIncorrect", minorTriadIncorrect);

        outState.putStringArrayList("majorTriadNoteChoices", majorTriadNoteChoices);
        outState.putStringArrayList("minorTriadNoteChoices", minorTriadNoteChoices);

        outState.putParcelable("quiz", quiz);

    }
}