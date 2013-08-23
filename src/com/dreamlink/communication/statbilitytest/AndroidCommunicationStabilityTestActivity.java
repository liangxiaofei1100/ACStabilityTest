package com.dreamlink.communication.statbilitytest;

import com.dreamlink.communication.api.AppUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This class is used for communication stability test.</br>
 * 
 * Client every 100ms send message to server. </br>
 * 
 * Server show the received data and write to log file, then send the received
 * data return to clients.</br>
 * 
 * Clients show the received data from server and write to log file.
 * 
 */
public class AndroidCommunicationStabilityTestActivity extends Activity {
	public static final String EXTRA_APP_ID = "app_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
	}

	private void launchActivity(boolean isServer) {
		Intent intent = new Intent();

		int appID = AppUtil.getAppID(this);
		intent.putExtra(EXTRA_APP_ID, appID);

		if (isServer) {
			intent.setClass(this, StabilityTestServer.class);
		} else {
			intent.setClass(this, StabilityTestClient.class);
		}
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_client:
			launchActivity(false);
			finish();
			break;
		case R.id.menu_server:
			launchActivity(true);
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stability_test_main, menu);
		return true;
	}
}