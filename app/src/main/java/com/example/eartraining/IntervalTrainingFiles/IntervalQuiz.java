package com.example.eartraining.IntervalTrainingFiles;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/*
Quiz that controls creating intervals
with the input options
 */
public class IntervalQuiz implements Parcelable {

    //random intervals created
    private List<Interval> quizIntervals;

    //total num questions
    private int numQuestions;
    //interval direction option
    private String intervalDirection;
    //if each interval starts on the same note
    private Boolean staticRoot;
    //types of intervals available for the quiz
    private ArrayList<String> intervalTypes;

    //default static root
    private int defaultStartingNote = 72; //60 = midi note of c4  changing to c5 = 72
    //options for start note
    private int[] startNoteOptions = {66, 67, 68, 69, 70, 71, 72, 73, 74, 75}; //c4 to c5

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

    public IntervalQuiz(int numQuestionsParam, String intervalDirectionParam, Boolean staticRootParam, ArrayList<String> intervalTypesParam) {
        numQuestions = numQuestionsParam;
        intervalDirection = intervalDirectionParam;
        staticRoot = staticRootParam;
        intervalTypes = intervalTypesParam;

        createQuiz();
    }

    protected IntervalQuiz(Parcel in) {
        quizIntervals = in.createTypedArrayList(Interval.CREATOR);
        numQuestions = in.readInt();
        intervalDirection = in.readString();
        byte tmpStaticRoot = in.readByte();
        staticRoot = tmpStaticRoot == 0 ? null : tmpStaticRoot == 1;
        intervalTypes = in.createStringArrayList();
        defaultStartingNote = in.readInt();
        startNoteOptions = in.createIntArray();
    }

    public static final Creator<IntervalQuiz> CREATOR = new Creator<IntervalQuiz>() {
        @Override
        public IntervalQuiz createFromParcel(Parcel in) {
            return new IntervalQuiz(in);
        }

        @Override
        public IntervalQuiz[] newArray(int size) {
            return new IntervalQuiz[size];
        }
    };

    /*
     Method to create random intervals for the quiz
     */
    public void createQuiz() {
        //android seeds automatically
        Random r = new Random();

        quizIntervals = new ArrayList<Interval>();

        //create n Intervals
        for (int i = 0; i < numQuestions; i++) {
            //find which interval to play
            int randomIndex = r.nextInt(intervalTypes.size());
            String randomIntervalName = intervalTypes.get(randomIndex);

            //select a starting note
            int startingNote = defaultStartingNote;
            if (!staticRoot) {
                int startingNoteIndex = r.nextInt(startNoteOptions.length);
                startingNote = startNoteOptions[startingNoteIndex];
            }

            //select a second note
            int secondNote = startingNote;
            //get the distance
            //check for error
            if (intervalMath.get(randomIntervalName) != null) {
                int distance = intervalMath.get(randomIntervalName);

                //choose to make the second note higher or lower
                switch (intervalDirection) {
                    case "ascending":
                        secondNote = startingNote + distance;
                        break;
                    case "descending":
                        secondNote = startingNote - distance;
                        break;
                    case "both":
                        boolean randomDirection = r.nextBoolean();

                        if (randomDirection){
                            //choose ascending
                            secondNote = startingNote + distance;
                        }
                        else {
                            //choose descending
                            secondNote = startingNote - distance;
                        }
                        break;
                }

            }
            else {
                Log.d("ERROR", "Invalid Key for intervalMath.get(randomIntervalName) with key " + randomIntervalName);
            }

            //create the interval
            Interval newInterval = new Interval(startingNote, secondNote, randomIntervalName);
            //add the interval to the quiz list
            quizIntervals.add(newInterval);

        }
    }

    public Interval getQuizInterval(int index) {
        return quizIntervals.get(index);
    }

    public List<Interval> getQuizIntervals() {
        return quizIntervals;
    }

    /*
    Checks if the input interval name matches
    the interval name at the input index
     */
    public boolean checkAnswer(String intervalName, int index) {
        Interval answer = quizIntervals.get(index);
        return intervalName.equals(answer.getName());
    }

    public void logQuizParameters() {
        Log.d("Quiz Parameters", "Number of Questions: " + numQuestions);
        Log.d("Quiz Parameters", "Interval Direction: " + intervalDirection);
        Log.d("Quiz Parameters", "Static Root: " + staticRoot);
        Log.d("Quiz Parameters", "Interval Types: " + intervalTypes);
        for (Interval interval : quizIntervals) {
            interval.printInterval();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(quizIntervals);
        parcel.writeInt(numQuestions);
        parcel.writeString(intervalDirection);
        parcel.writeByte((byte) (staticRoot == null ? 0 : staticRoot ? 1 : 2));
        parcel.writeStringList(intervalTypes);
        parcel.writeInt(defaultStartingNote);
        parcel.writeIntArray(startNoteOptions);
    }
}
