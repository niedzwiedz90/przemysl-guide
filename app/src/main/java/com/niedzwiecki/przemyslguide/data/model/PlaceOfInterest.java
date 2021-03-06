package com.niedzwiecki.przemyslguide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niedzwiecki on 11/1/2017.
 */

public class PlaceOfInterest implements Parcelable {

    public long id;
    public float lat;
    public float lon;
    public String type;
    public String name;
    public String description;
    public String image;
    public String email;
    public String telephone;
    public List<String> images;

    public PlaceOfInterest(int id, String image, String name, float lng, float lat) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.lon = lng;
        this.lat = lat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeFloat(lat);
        parcel.writeFloat(lon);
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(email);
        parcel.writeString(telephone);
        parcel.writeList(images);
    }

    private PlaceOfInterest(Parcel in) {
        id = in.readLong();
        lat = in.readFloat();
        lon = in.readFloat();
        type = in.readString();
        name = in.readString();
        description = in.readString();
        image = in.readString();
        email = in.readString();
        telephone = in.readString();
        images = new ArrayList<>();
        in.readList(this.images, String.class.getClassLoader());
    }

    public static final Parcelable.Creator<PlaceOfInterest> CREATOR = new Parcelable.Creator<PlaceOfInterest>() {
        public PlaceOfInterest createFromParcel(Parcel in) {
            return new PlaceOfInterest(in);
        }

        public PlaceOfInterest[] newArray(int size) {
            return new PlaceOfInterest[size];

        }
    };
}
