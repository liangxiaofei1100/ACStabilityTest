package com.dreamlink.communication.api;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppUtil {
	private static final String TAG = "AppUtil";
	public static final String META_DATA_APP_ID = "app_id";

	/**
	 * Get app id which set by activity.
	 * 
	 * @param activity
	 * @return
	 */
	public static int getAppID(Activity activity) {
		int appID = 0;
		try {
			ActivityInfo activityInfo = activity.getPackageManager()
					.getActivityInfo(activity.getComponentName(),
							PackageManager.GET_META_DATA);
			appID = activityInfo.metaData.getInt(META_DATA_APP_ID);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getAppID fail. " + e);
		}
		return appID;
	}

	/**
	 * Get app id which set by application.
	 * 
	 * @param application
	 * @return
	 */
	public static int getAppID(Application application) {
		int appID = 0;
		try {
			ApplicationInfo applicationInfo = application.getPackageManager()
					.getApplicationInfo(application.getPackageName(),
							PackageManager.GET_META_DATA);
			appID = applicationInfo.metaData.getInt(META_DATA_APP_ID);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getAppID fail. " + e);
		}
		return appID;
	}
}
