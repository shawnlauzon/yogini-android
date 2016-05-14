package com.santikama.yogini;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 5/10/16.
 */
public class Asana implements Parcelable {
    private int id;
    private String name;
    private int order;
    private int time;
    private String audio_begin;
    private String audio_end;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public int getTime() {
        return time;
    }

    public String getAudioBegin() {

        return audio_begin;
    }

    public String getAudioEnd() {
        return audio_end;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(name);
        out.writeInt(order);
        out.writeInt(time);
        out.writeString(audio_begin);
        out.writeString(audio_end);
    }

    public static final Parcelable.Creator<Asana> CREATOR
            = new Parcelable.Creator<Asana>() {
        public Asana createFromParcel(Parcel in) {
            return new Asana(in);
        }

        public Asana[] newArray(int size) {
            return new Asana[size];
        }
    };

    private Asana(Parcel in) {
        id = in.readInt();
        name = in.readString();
        order = in.readInt();
        time = in.readInt();
        audio_begin = in.readString();
        audio_end = in.readString();
    }

    @Override
    public String toString() {
        return "Asana{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}
