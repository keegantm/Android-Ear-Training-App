package com.example.eartraining.MenuRecyclerViewFiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/*
Data for recycler view of Ear Training activities
 */
public class MenuActivity implements Parcelable {
    private String activityTitle;
    private String activityDescription;

    /*
    Constructor for Item objects.

    String activityTitle : activityTitle to be displayed
    String activityDescription : activityDescription to be displayed
     */
    public MenuActivity(String activityTitle, String activityDescription) {
        this.activityTitle = activityTitle;
        this.activityDescription = activityDescription;
    }


    protected MenuActivity(Parcel in) {
        activityTitle = in.readString();
        activityDescription = in.readString();
    }

    public static final Creator<MenuActivity> CREATOR = new Creator<MenuActivity>() {
        @Override
        public MenuActivity createFromParcel(Parcel in) {
            return new MenuActivity(in);
        }

        @Override
        public MenuActivity[] newArray(int size) {
            return new MenuActivity[size];
        }
    };

    public String getTitle() {
        return activityTitle;
    }

    public String getDescription() {
        return activityDescription;
    }

    public void setTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public void setDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(activityTitle);
        parcel.writeString(activityDescription);
    }
}

