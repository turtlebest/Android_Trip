/**
 * Trip model.
 *
 * @author      Jessica Huang
 * @version     1.1
 */
package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

public class Trip implements Parcelable {

	private static final String TAG = "CreateTrip";

	private long tid;
	private String name;
	private Location location;
	private String time;
	private List<Person> friendList;
	
	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	
	/**
	 * Create a Trip model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Trip(Parcel p) {
		tid = p.readLong();
		name = p.readString();
		location = p.readParcelable(getClass().getClassLoader());
		time = p.readString();
		if (p.readByte() == 0x01) {
			friendList = new ArrayList<Person>();
			p.readList(friendList, Person.class.getClassLoader());
		} else {
			friendList = null;
		}
	}
	
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */
	public Trip(long tid, String name, Location location, String time, List<Person> friendList) {
		this.tid = tid;
		this.name = name;
		this.location = location;
		this.time = time;
		this.friendList = friendList;
		//Log.i(TAG,"aaa");
		Log.i(TAG,"trip name:" + name);
		Log.i(TAG,"trip time:" + time);
	}

	/**
	 * Serialize Trip object by using writeToParcel. 
	 * This function is automatically called by the
	 * system when the object is serialized.
	 * 
	 * @param dest Parcel object that gets written on 
	 * serialization. Use functions to write out the
	 * object stored via your member variables. 
	 * 
	 * @param flags Additional flags about how the object 
	 * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 * In our case, you should be just passing 0.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(tid);
		dest.writeString(name);
		dest.writeParcelable(location, flags);
		dest.writeString(time);
		if (friendList == null) {
			dest.writeByte((byte) (0x00));
		} else {
			dest.writeByte((byte) (0x01));
			dest.writeList(friendList);
		}
	}

	/**
	 * Feel free to add additional functions as necessary below.
	 */
	public long getTid(){
		return tid;
	}

	public void setTid(long tid){
		this.tid = tid;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Location getLocation(){
		return location;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public String getTime(){
		return time;
	}

	public void setTime(String time){
		this.time = time;
	}

	public List<Person> getFriendList(){
		return friendList;
	}

	public void setFriendList(List<Person> friendList){
		this.friendList = friendList;
	}

	/**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}
}
