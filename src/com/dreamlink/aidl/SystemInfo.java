package com.dreamlink.aidl;

import java.io.Serializable;

import android.os.Build;

public class SystemInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7084876749207553689L;

	public String mAndroidVersion;
	public int mAndroidVersionCode;
	public boolean mIsWiFiDirectSupported;

	public SystemInfo() {

	}

	public SystemInfo getLocalSystemInfo() {
		mAndroidVersion = Build.VERSION.RELEASE;
		mAndroidVersionCode = Build.VERSION.SDK_INT;
		// After Android 4.0, Wi-Fi direct is supported.
		mIsWiFiDirectSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
		return this;
	}

	@Override
	public String toString() {
		return "SystemInfo [mAndroidVersion=" + mAndroidVersion
				+ ", mAndroidVersionCode=" + mAndroidVersionCode
				+ ", mIsWiFiDirectSupported=" + mIsWiFiDirectSupported + "]";
	}
}
