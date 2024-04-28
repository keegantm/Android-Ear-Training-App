package com.example.eartraining;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/*
Fragment to be opened when the user makes an incorrect guess
 */
public class IncorrectFragment extends Fragment {

    private String incorrectType;
    private String correctText;
    private String incorrectText;


    /*
    Create the fragment and assign a layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.question_incorrect_fragment, parent, false);
    }

    /*
    After frag is created, set up the continue button and text views
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        //Retrieve Views
        Button continueButton = view.findViewById(R.id.continueButton);
        TextView correctTextView = view.findViewById(R.id.correctTextView);
        TextView incorrectTextView = view.findViewById(R.id.incorrectTextView);

        //retrieve data from parent activity
        Bundle args = getArguments();
        if (args != null) {
            //check if this is an interval
            incorrectType = args.getString("Incorrect Type");
            correctText = args.getString("Correct String");
            incorrectText = args.getString("Incorrect String");

            //interval quiz
            if (Objects.equals(incorrectType, "Interval")) {

                //get the text views and set their values
                String correctString = "The Correct Interval was a " + correctText;
                String incorrectString = "You chose a " + incorrectText;

                correctTextView.setText(correctString);
                incorrectTextView.setText(incorrectString);

            }

            //chord tone quiz
            if (Objects.equals(incorrectType, "ChordTone")) {
                //get the text views and set their values
                String correctString = "The Correct Chord Tone was a " + correctText;
                String incorrectString = "You chose a " + incorrectText;

                correctTextView.setText(correctString);
                incorrectTextView.setText(incorrectString);
            }
        }

        continueButton.setOnClickListener(v -> submit());
    }

    /*
    Button for sending data back to the hosting activity.
     */
    private void submit() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    /*
    Re-enable buttons on hosting activity when fragment is detatched
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Re-enable buttons from the hosting activity
        ((ButtonStateListener) requireActivity()).enableButtons(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("Incorrect Type", incorrectType);
        outState.putString("Correct String", correctText);
        outState.putString("Incorrect String", incorrectText);

    }
}

