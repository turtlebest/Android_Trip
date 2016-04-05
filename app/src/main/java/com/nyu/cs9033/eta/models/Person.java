/**
 * Person model.
 *
 * @author      Jessica Huang
 * @version     1.1
 */
package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
	
	// Member fields should exist here, what else do you need for a person?
	// Please add additional fields
	private long pid;
	private String name;
	private String phoneNumber;
	private Location curLocation;
	
	/**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
		public Person createFromParcel(Parcel p) {
			return new Person(p);
		}

		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
	
	/**
	 * Create a Person model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Person(Parcel p) {
		pid = p.readLong();
		name = p.readString();
		curLocation = p.readParcelable(getClass().getClassLoader());
		phoneNumber = p.readString();
	}
	
	/**
	 * Create a Person model object from arguments
	 * 
	 * @param name Add arbitrary number of arguments to
	 * instantiate Person class based on member variables.
	 */
	public Person(long pid, String name, Location curLocation, String phoneNumber) {
		this.pid = pid;
		this.name = name;
		this.curLocation = curLocation;
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Serialize Person object by using writeToParcel.  
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
		dest.writeLong(pid);
		dest.writeString(name);
		dest.writeParcelable(curLocation, flags);
		dest.writeString(phoneNumber);
	}

	/**
	 * Feel free to add additional functions as necessary below.
	 */
	public long getPid(){
		return pid;
	}

	public void setPid(long pid){
		this.pid = pid;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public Location getCurLocation(){
		return curLocation;
	}

	public void setCurLocation(Location curLocation){
		this.curLocation = curLocation;
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
