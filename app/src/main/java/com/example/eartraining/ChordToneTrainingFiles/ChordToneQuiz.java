package com.example.eartraining.ChordToneTrainingFiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/*
Notes:
Too low:
with the maj triad 67, 59, 62 the bottom two notes were kinda hard to hear
after adding overtones, lets see if it gets any better. If not, either change voicing logic to prevent this kind of voicing if root is too low, or change root options

Too high:
Notes: 75, 67, 70, 89. Maj 9, the chord tone way very piercing

Other:
 */

/*
Quiz that controls creating chord tones
with the input options
 */
public class ChordToneQuiz implements Parcelable {
    //random chord tones created
    private List<ChordTone> quizChordTones;

    //total num questions
    private int numQuestions;
    //if the root of each chord should be the same
    private Boolean staticRoot;

    //chord types available to make the quiz with
    HashMap<String, Boolean> chords;

    //chord tones available for the Major Triad
    ArrayList<String> majorTriadNoteChoices;

    //chord tones available for the Minor Triad
    ArrayList<String> minorTriadNoteChoices;

    //default static root
    private int defaultStartingNote = 72; //60 = midi note of c4  changing to c5 = 72
    //options for start note
    private int[] startNoteOptions = {68, 69, 70, 71, 72, 73, };

    //half step distance of each chord tone, from the root
    Map<String, Integer> chordToneMath = new HashMap<String, Integer>(){{
        put("Root", 0);
        put("♭3rd", 3);
        put("3rd", 4);
        put("5th", 7);
        put("♭7th", 10);
        put("7th", 11);
        put("♭9th", 13);
        put("9th", 14);
        put("♯9th", 15);
        put("11th", 5);
        put("♯11th", 6);
        put("♭13th", 8);
        put("13th", 9);
    }};

    public ChordToneQuiz(int numQuestions, boolean staticRoot, HashMap<String, Boolean> chords, ArrayList<String> majorTriadNoteChoices, ArrayList<String> minorTriadNoteChoices){
        this.numQuestions = numQuestions;
        this.staticRoot = staticRoot;
        this.chords = chords;
        this.majorTriadNoteChoices = majorTriadNoteChoices;
        this.minorTriadNoteChoices = minorTriadNoteChoices;

        createQuiz();
    }

    protected ChordToneQuiz(Parcel in) {
        numQuestions = in.readInt();
        byte tmpStaticRoot = in.readByte();
        staticRoot = tmpStaticRoot == 0 ? null : tmpStaticRoot == 1;
        majorTriadNoteChoices = in.createStringArrayList();
        minorTriadNoteChoices = in.createStringArrayList();
        defaultStartingNote = in.readInt();
        startNoteOptions = in.createIntArray();
    }

    public static final Creator<ChordToneQuiz> CREATOR = new Creator<ChordToneQuiz>() {
        @Override
        public ChordToneQuiz createFromParcel(Parcel in) {
            return new ChordToneQuiz(in);
        }

        @Override
        public ChordToneQuiz[] newArray(int size) {
            return new ChordToneQuiz[size];
        }
    };

    /*
    Create ChordTone objects, from the available options
     */
    public void createQuiz() {

        quizChordTones = new ArrayList<>();
        ArrayList<String> availableChords = new ArrayList<String>(2);

        String[] chordNames = {"Major Triad", "Minor Triad"};
        for (String chordName : chordNames){
            if (chords.get(chordName)) {
                availableChords.add(chordName);
            }
        }

        Random r = new Random();

        for (int i = 0; i < numQuestions; i++) {

            int root;
            int third;
            int fifth;
            if (staticRoot) {
                root = defaultStartingNote;
            }
            else {
                root =  startNoteOptions[r.nextInt(startNoteOptions.length)];
            }

            //need to have an array filled with available chord types so it can be expanded easily
            int randomChordIndex = r.nextInt(availableChords.size());
            String randomChordType = availableChords.get(randomChordIndex);

            int randomChordToneIndex;
            String chordTone = null;
            int[] voicing = new int[0];
            //use a switch statement so adding more in the future is easier
            switch (randomChordType) {
                case "Major Triad":
                    randomChordToneIndex = r.nextInt(majorTriadNoteChoices.size());
                    chordTone = majorTriadNoteChoices.get(randomChordToneIndex);
                    voicing = createMajTriadVoicing(root);
                    break;
                case "Minor Triad":
                    randomChordToneIndex = r.nextInt(minorTriadNoteChoices.size());
                    chordTone = minorTriadNoteChoices.get(randomChordToneIndex);
                    voicing = createMinTriadVoicing(root);
                    break;
            }

            third = voicing[0];
            fifth = voicing[1];

            //get an integer for the chord tone
            int chordToneInt = chordToneMath.get(chordTone) + root;

            ChordTone newChordTone = new ChordTone(root, third, fifth, chordToneInt, randomChordType, chordTone);
            quizChordTones.add(newChordTone);
        }

        logChordTones();
    }

    /*
    Select a voicing of a major triad
     */
    public int[] createMajTriadVoicing(int root) {
        int voicingCount = 3;
        Random r = new Random();
        int voicingSelection = r.nextInt(voicingCount);

        int third = 0;
        int fifth = 0;
        switch (voicingSelection + 1) {
            case 1:
                //first inversion ---> E G C
                third = root - 8;
                fifth = root - 5;
                break;
            case 2:
                //second inversion ----> G C E
                third = root + 4;
                fifth = root - 5;
                break;
            case 3:
                //root position ----> C E G
                third = root + 4;
                fifth = root + 7;
                break;
        }

        return new int[]{third, fifth};

    }

    /*
    Select a voicing of a minor triad
     */
    public int[] createMinTriadVoicing(int root) {
        int voicingCount = 3;
        Random r = new Random();
        int voicingSelection = r.nextInt(voicingCount);

        int third = 0;
        int fifth = 0;
        switch (voicingSelection + 1) {
            case 1:
                //first inversion ---> E G C
                third = root - 7;
                fifth = root - 5;
                break;
            case 2:
                //second inversion ----> G C E
                third = root + 3;
                fifth = root - 5;
                break;
            case 3:
                //root position ----> C E G
                third = root + 3;
                fifth = root + 7;
                break;
        }

        return new int[]{third, fifth};
    }

    public ChordTone getQuizChordTone(int position) {
        return quizChordTones.get(position);
    }

    public Boolean checkAnswer(String chordTone, String chordType, int position) {
        ChordTone correctChordTone = quizChordTones.get(position);
        return (Objects.equals(chordTone, correctChordTone.getChordToneName()) && Objects.equals(chordType, correctChordTone.getChordType()));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(numQuestions);
        parcel.writeByte((byte) (staticRoot == null ? 0 : staticRoot ? 1 : 2));
        parcel.writeStringList(majorTriadNoteChoices);
        parcel.writeStringList(minorTriadNoteChoices);
        parcel.writeInt(defaultStartingNote);
        parcel.writeIntArray(startNoteOptions);
    }

    public void logChordTones() {
        for (ChordTone chordTone : quizChordTones){
            chordTone.logChordTone();
        }
    }
}
