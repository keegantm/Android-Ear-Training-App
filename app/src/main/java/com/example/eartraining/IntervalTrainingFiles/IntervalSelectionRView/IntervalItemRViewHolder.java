package com.example.eartraining.IntervalTrainingFiles.IntervalSelectionRView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.R;

/*
View Holder for the Interval options recycler view
 */
public class IntervalItemRViewHolder extends RecyclerView.ViewHolder {

    private TextView intervalTitleTextView;
    private CheckBox intervalCheckBox;

    public IntervalItemRViewHolder(@NonNull View itemView) {
        super(itemView);

        this.intervalTitleTextView = itemView.findViewById(R.id.intervalCardTitle);
        this.intervalCheckBox = itemView.findViewById(R.id.intervalCheckBox);

    }

    public TextView getIntervalTitleTextView() {
        return intervalTitleTextView;
    }

    public void setIntervalTitleTextView(TextView intervalTitleTextView) {
        this.intervalTitleTextView = intervalTitleTextView;
    }

    public CheckBox getIntervalCheckBox() {
        return intervalCheckBox;
    }

    public void setIntervalCheckBox(CheckBox intervalCheckBox) {
        this.intervalCheckBox = intervalCheckBox;
    }
}
