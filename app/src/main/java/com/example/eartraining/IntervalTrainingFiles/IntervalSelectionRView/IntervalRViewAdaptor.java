package com.example.eartraining.IntervalTrainingFiles.IntervalSelectionRView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.R;

import java.util.List;

/*
Adapter for the Interval Options recycler view
 */
public class IntervalRViewAdaptor extends RecyclerView.Adapter<IntervalItemRViewHolder> {

    private final List<IntervalItem> intervals;
    private final Context context;

    public IntervalRViewAdaptor(List<IntervalItem> intervals, Context context) {
        this.intervals = intervals;
        this.context = context;
    }

    @NonNull
    @Override
    public IntervalItemRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.interval_selection_item, null);

        return new IntervalItemRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalItemRViewHolder holder, int position) {

        CheckBox itemCheckBox = holder.getIntervalCheckBox();

        // Get the IntervalItem at the current position
        IntervalItem thisInterval = intervals.get(position);

        //get attributes of this interval
        String intervalName = thisInterval.getTitle();
        Boolean intervalEnabled = thisInterval.getEnabled();

        // sets the name of the Item to the name textview of the viewholder.
        holder.getIntervalTitleTextView().setText(intervalName);

        // update the Enabled state of this interval when it is toggled
        // Remove the old listener to prevent changing the state of a different interval
        itemCheckBox.setOnCheckedChangeListener(null);
        //set the checkbox to reflect state of the interval
        itemCheckBox.setChecked(intervalEnabled);
        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // CheckBox is checked
                    Log.d("TEST", intervalName + " is enabled");
                    thisInterval.setEnabled(true);

                } else {
                    // CheckBox is unchecked
                    Log.d("TEST", intervalName + " is disabled");
                    thisInterval.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return intervals.size();
    }
}
