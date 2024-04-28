package com.example.eartraining;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.MenuRecyclerViewFiles.MenuActivity;
import com.example.eartraining.MenuRecyclerViewFiles.MenuRViewAdaptor;

import java.util.ArrayList;

/*
Main landing screen.
Allows the user to start ear training activities,
or to see stats for ear training activities
 */
public class MainActivity extends AppCompatActivity {
    ArrayList<MenuActivity> activities;
    //RecyclerView Adapter
    MenuRViewAdaptor viewAdapter;
    //Recycler View
    RecyclerView rView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null || !savedInstanceState.containsKey("Activities List")) {
            //If not restoring from a previous state, initialize the list of items as a new empty list.
            activities = new ArrayList<>();
        } else {
            //If Restoring a previous state, get the list from the bundle
            activities = savedInstanceState.getParcelableArrayList("Activities List");
        }

        if (activities != null && activities.isEmpty()) {
            MenuActivity i1 = new MenuActivity("Interval Identification", "Train your ear to identify different types of intervals.");
            MenuActivity i2 = new MenuActivity("Chord Tone Identification", "Train your ear to identify the relationship between chords and notes played after them.");

            activities.add(i1);
            activities.add(i2);
        }


        //horizontal r view
        //https://stackoverflow.com/questions/28460300/how-to-build-a-horizontal-listview-with-recyclerview
        rView = findViewById(R.id.activitiesRView);

        LinearLayoutManager layoutManger = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false){

            //https://stackoverflow.com/questions/51201482/android-percent-screen-width-in-recyclerview-item
            //override the space allocated for items in the recycler view
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
                lp.width = (int) (getWidth() * 0.75);
                return true;
            }
        };

        rView.setLayoutManager(layoutManger);

        viewAdapter = new MenuRViewAdaptor(activities, this);
        rView.setAdapter(viewAdapter);

    }

    /*
    Method to save the state in the case of configuration changes.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the list of items
        outState.putParcelableArrayList("Activities List", activities);
    }

    /*
    Method to restore data from onSaveInstanceState
    */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //If the list was saved, retrieve it
        if (savedInstanceState.containsKey("Activities List")) {
            activities = savedInstanceState.getParcelableArrayList("Activities List");
            //viewAdapter.notifyDataSetChanged();
        }

    }
}