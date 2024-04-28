package com.example.eartraining;

/*
Interface for Enabling/Disabling quiz questions when the correct/incorrect
fragment appears
 */
public interface ButtonStateListener {
    //void enableButtons(boolean correct, String input, String tag);
    void enableButtons(boolean correct);
    void disableButtons();
}
