package com.dreamlink.aidl;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Serializable, Parcelable {

	private static final long serialVersionUID = 1967485881803917696L;

	private String mName = "Unkown";
	public static final int NETWORK_MODE_UNKOWN = 0;
	public static final int NETWORK_MODE_AP = 1;
	public static final int NETWORK_MODE_STA = 2;
	public static final int NETWORK_MODE_WIFI_DIRECT = 3;
	public static final int NETWORK_MODE_STA_AND_WIFI_DIRECT = 4;
	private int mNetworkMode = NETWORK_MODE_UNKOWN;
	private int mUserID;

	private SystemInfo mSystemInfo;

	public User() {

	}

	public int getUserID() {
		return mUserID;
	}

	public void setUserID(int userID) {
		mUserID = userID;
	}

	public void setUserName(String name) {
		mName = name;
	}

	public String getUserName() {
		return mName;
	}

	public int getNetworkMode() {
		return mNetworkMode;
	}

	public void setNetworkMode(int mode) {
		mNetworkMode = mode;
	}

	public void setSystemInfo(SystemInfo systemInfo) {
		mSystemInfo = systemInfo;
	}

	public SystemInfo getSystemInfo() {
		return mSystemInfo;
	}

	@Override
	public String toString() {
		return "User [mName=" + mName + ", mNetworkMode=" + mNetworkMode
				+ ", mUserID=" + mUserID + ", mSystemInfo=" + mSystemInfo + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mUserID);
		dest.writeString(mName);
		dest.writeSerializable(mSystemInfo);
	}

	public void readFromParcel(Parcel source) {
		mUserID = source.readInt();
		mName = source.readString();
		mSystemInfo = (SystemInfo) source.readSerializable();
	}

	public User(Parcel source) {
		readFromParcel(source);
	}

	public static Creator<User> CREATOR = new Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			// TODO Auto-generated method stub
			return new User[size];
		}

	};
}
