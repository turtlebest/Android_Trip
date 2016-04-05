/**
 * Location model.
 *
 * @author      Jessica Huang
 * @version     1.0
 */
package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private String name;
    private String address;
    private String latitude;
    private String longitude;

    /**
     * Create a Location model object from arguments
     *
     * @param name Add arbitrary number of arguments to
     * instantiate Person class based on member variables.
     */
    public Location(String name, String address, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Location(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

}
