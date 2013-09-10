package com.dreamlink.communication.statbilitytest;

import java.util.ArrayList;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.CommunicationManager.OnCommunicationListener;
import com.dreamlink.communication.lib.CommunicationManager.OnConnectionChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * see {@code StabilityTestActivity}.
 * 
 */
public class StabilityTestServer extends Activity implements
		OnCommunicationListener, OnConnectionChangeListener {
	private static final String TAG = "StabilityTestServer";
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mData;

	/** Stability test app id */
	private int mAppID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mAppID = intent.getIntExtra(StabilityTestActivity.EXTRA_APP_ID, 0);

		initView();

		mCommunicationManager = new CommunicationManager(
				getApplicationContext());
		mCommunicationManager.connectCommunicatonService(this, this, mAppID);
	}

	private void initView() {
		setContentView(R.layout.test_stability);
		initData();
		mListView = (ListView) findViewById(R.id.lstStabilityTest);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mData);
		mListView.setAdapter(mAdapter);
	}

	private void initData() {
		mData = new ArrayList<String>();

	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String messageString = msg.obj.toString();

			if (mData.size() > 100) {
				mData.clear();
			}
			mData.add(messageString);
			mAdapter.notifyDataSetChanged();

			sendMessageToAll(messageString.getBytes());
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCommunicationManager.disconnectCommunicationService();
	}

	// Communication Service begin
	private CommunicationManager mCommunicationManager;

	private void sendMessageToSingle(byte[] data, User user) {
		sendMessage(data, user);
	}

	private void sendMessageToAll(byte[] data) {
		sendMessage(data, null);
	}

	private void sendMessage(byte[] data, User user) {
		mCommunicationManager.sendMessage(data, mAppID, user);
	}

	@Override
	public void onCommunicationDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommunicationConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveMessage(byte[] msg, User sendUser) {
		if (sendUser == null) {
			Log.d(TAG, "User is lost connection.");
			return;
		}
		Message message = mHandler.obtainMessage();
		message.obj = "From " + sendUser.getUserName() + ": " + new String(msg);
		mHandler.sendMessage(message);

	}

	@Override
	public void onUserConnected(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserDisconnected(User user) {
		// TODO Auto-generated method stub

	}
	// Communication Service end
}
