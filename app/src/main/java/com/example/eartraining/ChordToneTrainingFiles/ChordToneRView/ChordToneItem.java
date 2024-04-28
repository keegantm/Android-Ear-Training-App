package com.example.eartraining.ChordToneTrainingFiles.ChordToneRView;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/*
Chord Tone data for the chord tone options recycler views
 */
public class ChordToneItem implements Parcelable {
    private String title;
    private Boolean enabled;
    private String parentChord;

    public ChordToneItem(String title, Boolean enabled, String parentChord) {
        this.title = title;
        this.enabled = enabled;
        this.parentChord = parentChord;
    }

    protected ChordToneItem(Parcel in) {
        title = in.readString();
        byte tmpEnabled = in.readByte();
        enabled = tmpEnabled == 0 ? null : tmpEnabled == 1;
        parentChord = in.readString();
    }

    public static final Creator<ChordToneItem> CREATOR = new Creator<ChordToneItem>() {
        @Override
        public ChordToneItem createFromParcel(Parcel in) {
            return new ChordToneItem(in);
        }

        @Override
        public ChordToneItem[] newArray(int size) {
            return new ChordToneItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getParentChord() {
        return parentChord;
    }

    public void setParentChord(String parentChord) {
        this.parentChord = parentChord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeByte((byte) (enabled == null ? 0 : enabled ? 1 : 2));
        parcel.writeString(parentChord);
    }
}
