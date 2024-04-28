package com.example.eartraining.MenuRecyclerViewFiles;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eartraining.R;

/*
View holder for ear training activity option
 */
public class MenuRViewHolder extends RecyclerView.ViewHolder{

    private TextView cardTitle;
    private View divider;
    private Button goButton;
    private Button statsButton;
    private TextView description;


    public MenuRViewHolder(@NonNull View itemView) {
        super(itemView);

        this.cardTitle = itemView.findViewById(R.id.cardActivityTitle);
        this.divider = itemView.findViewById(R.id.divider);
        this.description = itemView.findViewById(R.id.cardDescription);
        this.goButton = itemView.findViewById(R.id.cardGoButton);
        this.statsButton = itemView.findViewById(R.id.cardStatsButton);
    }

    public TextView getCardTitle() {
        return cardTitle;
    }

    public Button getGoButton() {
        return goButton;
    }

    public Button getStatsButton() {
        return statsButton;
    }

    public View getDivider() {
        return divider;
    }

    public TextView getDescription() {
        return description;
    }
}
