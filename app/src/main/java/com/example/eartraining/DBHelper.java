package com.example.eartraining;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/*
Class to access the Database
 */
public class DBHelper extends SQLiteOpenHelper {
    //all interval names
    private static final String[] intervalNames = {
            "Unison", "Minor Second", "Major Second", "Minor Third", "Major Third",
            "Perfect Fourth", "Tritone", "Perfect Fifth", "Minor Sixth", "Major Sixth",
            "Minor Seventh", "Major Seventh", "Octave", "Minor Ninth", "Major Ninth"
    };

    //all major triad chord tones
    String[] majTriadChordToneKeys = {
            "Root", "3rd", "5th", "♭7th", "7th", "♭9th", "9th", "♯9th", "♯11th", "♭13th", "13th"
    };
    //all minor triad chord tones
    String[] minTriadChordToneKeys = {
            "Root", "♭3rd", "5th", "♭7th", "7th", "♭9th", "9th", "11th", "♯11th", "♭13th", "13th"
    };

    private static final String INTERVALTABLE = "Intervals";
    private static final String INTERVALNAME = "INTERVAL_NAME";


    private static final String CHORDTONETABLE = "Chord_Tones";
    private static final String CHORDTONENAME = "Chord_Tone_Name";
    private static final String CHORDTYPE = "Chord_Type";

    private static final String DB_NAME = "EarTraining.db";
    private static final String CORRECT = "CORRECT";
    private static final String DATE = "DATE";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 3);
    }

    /*
    Method to create sqlLite tables for storing the user's
    correct & incorrect guesses
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + INTERVALTABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                INTERVALNAME + " TEXT," +
                CORRECT + " INTEGER, " +
                DATE + " TEXT)");

        db.execSQL("CREATE TABLE " + CHORDTONETABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                CHORDTYPE + " TEXT," +
                CHORDTONENAME + " TEXT," +
                CORRECT + " INTEGER, " +
                DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + INTERVALTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CHORDTONETABLE);

        // Recreate the table
        onCreate(db);
    }

    /*
    Method to parse arraylists of correct interval guesses and incorrect interval guesses

    ArrayList<String> correct - names of correctly identified intervals
    ArrayList<String> correct - names of incorrectly identified intervals
    LocalDate date - the date these guesses were made
     */
    public boolean addIntervalValues(ArrayList<String> correct, ArrayList<String> incorrect, LocalDate date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the date as a string
            String formattedDate = date.format(formatter);

            ContentValues values = new ContentValues();
            // Insert correct guesses
            for (String correctIntervalGuess : correct) {
                Log.d("DB DEBUG", "Correct : " + correctIntervalGuess);

                values.put(INTERVALNAME, correctIntervalGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(INTERVALTABLE, null, values);
            }

            // Insert incorrect guesses
            for (String incorrectIntervalGuess : incorrect) {
                Log.d("DB DEBUG", "Incorrect : " + incorrectIntervalGuess);

                values.put(INTERVALNAME, incorrectIntervalGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(INTERVALTABLE, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d("DATABASE ERROR", e.toString());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /*
    Method to parse arraylists of correct & incorrect chord tone guesses into the database

    ArrayList<String> majorTriadCorrect - names of correctly identified chord tones over major triads
    ArrayList<String> majorTriadIncorrect - names of incorrectly identified chord tones over major triads
    ArrayList<String> minorTriadCorrect - names of correctly identified chord tones over minor triads
    ArrayList<String> minorTriadIncorrect - names of incorrectly identified chord tones over minor triads
    LocalDate date - the date these guesses were made
     */
    public boolean addChordToneValues(ArrayList<String> majorTriadCorrect, ArrayList<String> majorTriadIncorrect, ArrayList<String> minorTriadCorrect, ArrayList<String> minorTriadIncorrect, LocalDate date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the date as a string
            String formattedDate = date.format(formatter);

            ContentValues values = new ContentValues();

            //insert correct maj triad chord tone guesses
            for (String correctMajorTriadGuess : majorTriadCorrect) {
                Log.d("DB DEBUG", "Correct : " + correctMajorTriadGuess);

                values.put(CHORDTYPE, "Major Triad");
                values.put(CHORDTONENAME, correctMajorTriadGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert incorrect maj triad chord tone guesses
            for (String incorrectMajorTriadGuess : majorTriadIncorrect) {
                Log.d("DB DEBUG", "Incorrect : " + incorrectMajorTriadGuess);

                values.put(CHORDTYPE, "Major Triad");
                values.put(CHORDTONENAME, incorrectMajorTriadGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert correct minor triad chord tone guesses
            for (String correctMinorTriadGuess : minorTriadCorrect) {
                Log.d("DB DEBUG", "Correct : " + correctMinorTriadGuess);

                values.put(CHORDTYPE, "Minor Triad");
                values.put(CHORDTONENAME, correctMinorTriadGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert incorrect minor triad chord tone guesses
            for (String incorrectMinorTriadGuess : minorTriadIncorrect) {
                Log.d("DB DEBUG", "Incorrect : " + incorrectMinorTriadGuess);

                values.put(CHORDTYPE, "Minor Triad");
                values.put(CHORDTONENAME, incorrectMinorTriadGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d("DATABASE ERROR", e.toString());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getAllIntervalEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + INTERVALTABLE, null);
    }

    public Cursor getAllChordToneEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + CHORDTONETABLE, null);
    }

    /*
    Method to get a Hashmap of the number of times all intervals have been incorrect OR correct
    after a certain date

    LocalDate date - date to query after
    int correctBool - boolean for if the guesses retrieved should be correct or incorrect
     */
    public HashMap<String, Integer> getIntervalCountsAfterDate(LocalDate date, int correctBool) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the input date as a string
            String formattedDate = date.format(formatter);

            // Initialize interval counts map with all interval names and zero counts
            HashMap<String, Integer> intervalCounts = new HashMap<>();
            for (String intervalName : intervalNames) {
                intervalCounts.put(intervalName, 0);
            }

            // Query to retrieve counts of each interval after the input date where CORRECT is 1
            String query = "SELECT " + INTERVALNAME + ", COUNT(*) AS count FROM " + INTERVALTABLE +
                    " WHERE " + DATE + " > ? AND " + CORRECT + " = ? GROUP BY " + INTERVALNAME;
            String[] selectionArgs = new String[]{formattedDate, String.valueOf(correctBool)};

            Cursor cursor = db.rawQuery(query, selectionArgs);

            // Iterate through the cursor to update counts
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String intervalName = cursor.getString(cursor.getColumnIndex(INTERVALNAME));
                Log.d("DB DEBUG", "cursor got : " + intervalName);
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex("count"));
                Log.d("DB DEBUG", "cursor got count: " + count);
                intervalCounts.put(intervalName, count);
            }
            cursor.close();
            return intervalCounts;
        } catch (Exception e) {
            Log.d("DB ERROR", e.toString());
            return null;
        }
    }

    /*
    Method to get a Hashmap of the number of times all chordTones have been incorrect OR correct
    after a certain date, for a certain type of chord tone (Major Triad or Minor Triad)

    LocalDate date - date to query after
    int correctBool - boolean for if the guesses retrieved should be correct or incorrect
    String chordType - name of chord associated with the chord tone guess
     */
    public HashMap<String, Integer> getChordToneCountsAfterDate(LocalDate date, int correctBool, String chordType) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the input date as a string
            String formattedDate = date.format(formatter);

            HashMap<String, Integer> chordToneCounts = new HashMap<>();
            if (Objects.equals(chordType, "Major Triad")){
                for (String chordTone : majTriadChordToneKeys) {
                    chordToneCounts.put(chordTone, 0);
                }
            } else if (Objects.equals(chordType, "Minor Triad")) {
                for (String chordTone : minTriadChordToneKeys) {
                    chordToneCounts.put(chordTone, 0);
                }
            }

//            String query = "SELECT " + INTERVALNAME + ", COUNT(*) AS count FROM " + INTERVALTABLE +
//                    " WHERE " + DATE + " > ? AND " + CORRECT + " = ? GROUP BY " + INTERVALNAME;
            String query = "SELECT " + CHORDTONENAME + ", COUNT(*) AS count FROM " + CHORDTONETABLE +
                    " WHERE " + DATE + " > ? AND " + CORRECT + " = ? AND " + CHORDTYPE + " = ? GROUP BY " + CHORDTONENAME;
            String[] selectionArgs = new String[]{formattedDate, String.valueOf(correctBool), chordType};

            Cursor cursor = db.rawQuery(query, selectionArgs);

            // Iterate through the cursor to update counts
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String chordToneName = cursor.getString(cursor.getColumnIndex(CHORDTONENAME));
                Log.d("DB DEBUG", "cursor got : " + chordToneName);
                @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex("count"));
                Log.d("DB DEBUG", "cursor got count: " + count);
                chordToneCounts.put(chordToneName, count);
            }

            cursor.close();
            return chordToneCounts;

        } catch (Exception e) {
            Log.d("DB ERROR", e.toString());
            return null;
        }
    }

    /*
    Put mock data in for the interval quiz
     */
    public boolean addMockIntervalData(LocalDate date) {

        Random random = new Random();

        // Create lists for correct and incorrect intervals with 20 random elements each
        ArrayList<String> correct = new ArrayList<>();
        ArrayList<String> incorrect = new ArrayList<>();

        // Add 20 random elements to each list
        for (int i = 0; i < 20; i++) {
            correct.add(intervalNames[random.nextInt(intervalNames.length)]);
            incorrect.add(intervalNames[random.nextInt(intervalNames.length)]);
        }

        //LocalDate date = LocalDate.now().minusMonths(2);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the date as a string
            String formattedDate = date.format(formatter);

            ContentValues values = new ContentValues();
            // Insert correct guesses
            for (String correctIntervalGuess : correct) {

                values.put(INTERVALNAME, correctIntervalGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(INTERVALTABLE, null, values);
            }

            // Insert incorrect guesses
            for (String incorrectIntervalGuess : incorrect) {

                values.put(INTERVALNAME, incorrectIntervalGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(INTERVALTABLE, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d("DATABASE ERROR", e.toString());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /*
     Put mock data in for the chord tone quiz
     */
    public boolean addMockChordToneData(LocalDate date) {

        ArrayList<String> majorTriadCorrect = new ArrayList<>();
        ArrayList<String> majorTriadIncorrect = new ArrayList<>();
        ArrayList<String> minorTriadCorrect = new ArrayList<>();
        ArrayList<String> minorTriadIncorrect = new ArrayList<>();

        Random random = new Random();

        // Fill the lists with 20 random elements each
        for (int i = 0; i < 20; i++) {
            majorTriadCorrect.add(majTriadChordToneKeys[random.nextInt(majTriadChordToneKeys.length)]);
            majorTriadIncorrect.add(majTriadChordToneKeys[random.nextInt(majTriadChordToneKeys.length)]);
        }
        for (int i = 0; i < 20; i++) {
            minorTriadCorrect.add(minTriadChordToneKeys[random.nextInt(minTriadChordToneKeys.length)]);
            minorTriadIncorrect.add(minTriadChordToneKeys[random.nextInt(minTriadChordToneKeys.length)]);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Format the date as a string
            String formattedDate = date.format(formatter);

            ContentValues values = new ContentValues();

            //insert correct maj triad chord tone guesses
            for (String correctMajorTriadGuess : majorTriadCorrect) {
                Log.d("DB DEBUG", "Correct : " + correctMajorTriadGuess);

                values.put(CHORDTYPE, "Major Triad");
                values.put(CHORDTONENAME, correctMajorTriadGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert incorrect maj triad chord tone guesses
            for (String incorrectMajorTriadGuess : majorTriadIncorrect) {
                Log.d("DB DEBUG", "Incorrect : " + incorrectMajorTriadGuess);

                values.put(CHORDTYPE, "Major Triad");
                values.put(CHORDTONENAME, incorrectMajorTriadGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert correct minor triad chord tone guesses
            for (String correctMinorTriadGuess : minorTriadCorrect) {

                values.put(CHORDTYPE, "Minor Triad");
                values.put(CHORDTONENAME, correctMinorTriadGuess);
                values.put(CORRECT, 1);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            //insert incorrect minor triad chord tone guesses
            for (String incorrectMinorTriadGuess : minorTriadIncorrect) {

                values.put(CHORDTYPE, "Minor Triad");
                values.put(CHORDTONENAME, incorrectMinorTriadGuess);
                values.put(CORRECT, 0);
                values.put(DATE, formattedDate);
                db.insert(CHORDTONETABLE, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d("DATABASE ERROR", e.toString());
            return false;
        } finally {
            db.endTransaction();
        }
    }
}
