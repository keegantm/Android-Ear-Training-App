package com.example.eartraining.ChordToneTrainingFiles;

import android.util.Log;

/*
Chord Tone Objects to be used by the quiz
 */
public class ChordTone {
    //first midi note of the chord
    private int firstNote;
    //second midi note of the chord
    private int secondNote;
    //third midi note of the chord
    private int thirdNote;

    //midi note of the chord tone
    private int chordToneInt;

    //chord type
    private String chordType;
    //tone to relate to the chord.
    private String chordToneName;

    public ChordTone(int firstNoteParam, int secondNoteParam, int thirdNoteParam, int chordToneInt, String chordType, String chordToneName) {
        this.firstNote = firstNoteParam;
        this.secondNote = secondNoteParam;
        this.thirdNote = thirdNoteParam;
        this.chordToneInt = chordToneInt;
        this.chordType = chordType;
        this.chordToneName = chordToneName;
    }
    public int getFirstNote() {
        return firstNote;
    }

    public void setFirstNote(int firstNote) {
        this.firstNote = firstNote;
    }

    public int getSecondNote() {
        return secondNote;
    }

    public void setSecondNote(int secondNote) {
        this.secondNote = secondNote;
    }

    public int getThirdNote() {
        return thirdNote;
    }

    public void setThirdNote(int thirdNote) {
        this.thirdNote = thirdNote;
    }

    public int getChordToneInt() {
        return chordToneInt;
    }

    public void setChordToneInt(int chordToneInt) {
        this.chordToneInt = chordToneInt;
    }

    public String getChordType() {
        return chordType;
    }

    public void setChordType(String chordType) {
        this.chordType = chordType;
    }

    public String getChordToneName() {
        return chordToneName;
    }

    public void setChordToneName(String chordToneName) {
        this.chordToneName = chordToneName;
    }

    public Integer[] getNotes() {

        return new Integer[]{firstNote, secondNote, thirdNote, chordToneInt};
    }

    public void logChordTone() {
        Log.d("Chord Tone", "Chord Tone Type: " + chordToneName);
        Log.d("Chord Tone", "Chord Type: " + chordType);
        Log.d("Chord Tone", "Notes: " + firstNote + ", " + secondNote + ", " + thirdNote + ", " + chordToneInt);
        Log.d("Chord Tone", "---------------------------------");
    }

}
