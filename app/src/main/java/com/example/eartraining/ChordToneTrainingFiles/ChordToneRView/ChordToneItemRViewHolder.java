package com.example.eartraining.ChordToneTrainingFiles.ChordToneRView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.R;

/*
View Holder for the Chord Tone options recycler views
 */
public class ChordToneItemRViewHolder extends RecyclerView.ViewHolder{

    private TextView chordToneText;
    private CheckBox chordToneCheckBox;

    public TextView getChordToneTextView() {
        return chordToneText;
    }

    public void setChordToneText(TextView chordToneText) {
        this.chordToneText = chordToneText;
    }

    public CheckBox getChordToneCheckBox() {
        return chordToneCheckBox;
    }

    public void setChordToneCheckBox(CheckBox chordToneCheckBox) {
        this.chordToneCheckBox = chordToneCheckBox;
    }

    public ChordToneItemRViewHolder(@NonNull View itemView) {
        super(itemView);

        this.chordToneText = itemView.findViewById(R.id.intervalCardTitle);
        this.chordToneCheckBox = itemView.findViewById(R.id.intervalCheckBox);

    }
}
