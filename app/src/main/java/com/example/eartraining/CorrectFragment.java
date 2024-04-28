package com.example.eartraining;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/*
Fragment to be opened when the user gets a question correct
 */
public class CorrectFragment extends Fragment {

    /*
    Create the fragment and assign a layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.question_correct_fragment, parent, false);
    }

    /*
    After frag is created, set up the continue button
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        Button continueButton = view.findViewById(R.id.continueButton);

        continueButton.setOnClickListener(v -> submit());
    }

    /*
    Method to remove the fragment
     */
    private void submit() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    /*
    Executed when the fragment is detached from the hosting activity.
    Tells the hosting activity to re-enable its buttons
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Re-enable buttons from the hosting activity
        ((ButtonStateListener) requireActivity()).enableButtons(false);
    }
}
