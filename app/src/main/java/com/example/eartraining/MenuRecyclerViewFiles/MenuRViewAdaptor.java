package com.example.eartraining.MenuRecyclerViewFiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;

import com.example.eartraining.ChordToneStats;
import com.example.eartraining.ChordToneTrainingFiles.ChordToneTrainingMenuActivity;
import com.example.eartraining.IntervalStatsActivity;
import com.example.eartraining.IntervalTrainingFiles.IntervalTrainingMenu;
import com.example.eartraining.R;

import java.util.List;

/*
Adaptor for R-View displaying ear training activities
 */
public class MenuRViewAdaptor extends RecyclerView.Adapter<MenuRViewHolder>{
    //list of items in the RView
    private final List<MenuActivity> activities;
    private final Context context;


    /*
    Constructor for the LinkCollectorAdaptor.

    Requires a List of Items that can be displayed, the context, and an implementation of RViewClickInterface
    to allow the items link text to be clicked to launch the link
     */
    public MenuRViewAdaptor(List<MenuActivity> activities, Context context) {
        this.activities = activities;
        this.context = context;
    }

    @NonNull
    /*
    Create ViewHolders to display our items
    */
    @Override
    public MenuRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create an instance of the view-holder by passing it the layout inflated as view and no root.
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.menu_activity_r_view, null);

        return new MenuRViewHolder(view);
    }

    /*
    Assigns values to the ViewHolders based on the position of the recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull MenuRViewHolder holder, int position) {
        // sets the name of the Item to the name textview of the viewholder.
        holder.getCardTitle().setText(activities.get(position).getTitle());
        // sets the url of the Item to the url textview of the viewholder.
        holder.getDescription().setText(String.valueOf(activities.get(position).getDescription()));

        Button cardButton = holder.getGoButton();
        cardButton.setOnClickListener(v -> {
            Log.d("AT POSITION :", String.valueOf(position));

            //this button starts Interval training
            if (position == 0) {
                Intent intent = new Intent(v.getContext(), IntervalTrainingMenu.class);
                v.getContext().startActivity(intent);
            } else if (position == 1) {
                //this button starts Chord Tone training
                Intent intent = new Intent(v.getContext(), ChordToneTrainingMenuActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        Button statsButton = holder.getStatsButton();
        statsButton.setOnClickListener(v -> {
            if (position == 0) {
                //stats for interval activity
                Intent intent = new Intent(v.getContext(), IntervalStatsActivity.class);
                v.getContext().startActivity(intent);
            }
            else if (position == 1) {
                //stats for chord tone activity
                Intent intent = new Intent(v.getContext(), ChordToneStats.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    /*
    Return the number of items in the Recycler View
     */
    @Override
    public int getItemCount() {
        return activities.size();
    }
}

