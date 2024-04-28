package com.example.eartraining.ChordToneTrainingFiles.ChordToneRView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.R;

import java.util.List;

/*
Adapter for the Chord Tone Options recycler views
 */
public class ChordToneRViewAdapter extends RecyclerView.Adapter<ChordToneItemRViewHolder>{

    private final List<ChordToneItem> chordTones;
    private final Context context;

    public ChordToneRViewAdapter(List<ChordToneItem> chordTones, Context context) {
        this.chordTones = chordTones;
        this.context = context;
    }

    @NonNull
    @Override
    public ChordToneItemRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.interval_selection_item, null);

        return new ChordToneItemRViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChordToneItemRViewHolder holder, int position) {
        CheckBox itemCheckBox = holder.getChordToneCheckBox();

        // Get the ChordToneItem at the current position
        ChordToneItem thisChordTone = chordTones.get(position);

        //get attributes of this chord Tone
        String chordToneName = thisChordTone.getTitle();
        Boolean chordToneEnabled = thisChordTone.getEnabled();

        // sets the name of the Item to the name textview of the viewholder.
        holder.getChordToneTextView().setText(chordToneName);

        // update the Enabled state of this chord tone when it is toggled
        // Remove the old listener to prevent changing the state of a different chord tone
        itemCheckBox.setOnCheckedChangeListener(null);
        //set the checkbox to reflect state of the chord tone
        itemCheckBox.setChecked(chordToneEnabled);
        itemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // CheckBox is checked
                Log.d("TEST", chordToneName + " is enabled");
                thisChordTone.setEnabled(true);

            } else {
                // CheckBox is unchecked
                Log.d("TEST", chordToneName + " is disabled");
                thisChordTone.setEnabled(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chordTones.size();
    }
}
