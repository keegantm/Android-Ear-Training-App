package com.example.eartraining.IntervalTrainingFiles;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

/*
Interval Object to be used by the quiz
 */
public class Interval implements Parcelable {
    //first note of the interval
    private int firstNote;
    //second note of the interval
    private int secondNote;
    //interval name/type
    private String name;

    protected Interval(Parcel in) {
        firstNote = in.readInt();
        secondNote = in.readInt();
        name = in.readString();
    }

    public static final Creator<Interval> CREATOR = new Creator<Interval>() {
        @Override
        public Interval createFromParcel(Parcel in) {
            return new Interval(in);
        }

        @Override
        public Interval[] newArray(int size) {
            return new Interval[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Interval(int firstNoteParam, int secondNoteParam, String nameParam) {
        firstNote = firstNoteParam;
        secondNote = secondNoteParam;
        name = nameParam;
    }

    public void printInterval() {
        Log.d("Interval Details", "-----------------------------");
        Log.d("Interval Details", "First Note: " + firstNote);
        Log.d("Interval Details", "Second Note: " + secondNote);
        Log.d("Interval Details", "Name: " + name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(firstNote);
        parcel.writeInt(secondNote);
        parcel.writeString(name);
    }
}
