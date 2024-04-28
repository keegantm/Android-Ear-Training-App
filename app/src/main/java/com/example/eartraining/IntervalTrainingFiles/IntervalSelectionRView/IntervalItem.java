package com.example.eartraining.IntervalTrainingFiles.IntervalSelectionRView;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/*
Interval data for the interval options recycler view of possible intervals
 */
public class IntervalItem implements Parcelable {
    private String title;
    //checkbox changes this
    private Boolean enabled;

    public IntervalItem(String title, Boolean enabled) {
        this.title = title;
        this.enabled = enabled;
    }

    protected IntervalItem(Parcel in) {
        title = in.readString();
        byte tmpEnabled = in.readByte();
        enabled = tmpEnabled == 0 ? null : tmpEnabled == 1;
    }

    public static final Creator<IntervalItem> CREATOR = new Creator<IntervalItem>() {
        @Override
        public IntervalItem createFromParcel(Parcel in) {
            return new IntervalItem(in);
        }

        @Override
        public IntervalItem[] newArray(int size) {
            return new IntervalItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeByte((byte) (enabled == null ? 0 : enabled ? 1 : 2));
    }
}
