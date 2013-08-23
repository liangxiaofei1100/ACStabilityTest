package com.dreamlink.communication.statbilitytest;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dreamlink.aidl.Communication;
import com.dreamlink.aidl.OnCommunicationListenerExternal;
import com.dreamlink.aidl.User;
import com.dreamlink.communication.api.CommunicateService;
import com.dreamlink.communication.api.Log;
import com.dreamlink.communication.api.LogFile;
import com.dreamlink.communication.api.TimeUtil;

/**
 * see {@code AndroidCommunicationStabilityTestActivity}.
 * 
 */
public class StabilityTestServer extends Activity {
	private static final String TAG = "StabilityTestServer";
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mData;
	private LogFile mDataLogFile;
	private LogFile mErrorLogFile;

	/** Stability test app id */
	private int mAppID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mAppID = intent.getIntExtra(
				AndroidCommunicationStabilityTestActivity.EXTRA_APP_ID, 0);

		initView();

		connectCommunicationService();
		mDataLogFile = new LogFile(getApplicationContext(),
				"StabilityTestServer-" + TimeUtil.getCurrentTime() + ".txt");
		mDataLogFile.open();

		mErrorLogFile = new LogFile(getApplicationContext(),
				"StabilityTestServer_error-" + TimeUtil.getCurrentTime()
						+ ".txt");
		mErrorLogFile.open();
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
			mDataLogFile.writeLog(TimeUtil.getCurrentTime() + " Received: \n");
			mDataLogFile.writeLog(messageString);

			// mCommunicationManager.sendMessage(messageString.getBytes(), 0);
			sendMessageToAll(messageString.getBytes());
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disconnectService();
	}

	// Communication Service begin
	private Communication mCommunication;
	private ServiceConnection mServiceConnection;

	private void sendMessageToSingle(byte[] data, User user) {
		sendMessage(data, user);
	}

	private void sendMessageToAll(byte[] data) {
		sendMessage(data, null);
	}

	private void sendMessage(byte[] data, User user) {
		try {
			mCommunication.sendMessage(data, mAppID, user);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean connectCommunicationService() {
		mServiceConnection = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				try {
					mCommunication
							.unRegistListenr(mCommunicationListenerExternal);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				mCommunication = Communication.Stub.asInterface(service);
				try {
					mCommunication.registListenr(
							mCommunicationListenerExternal, mAppID);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		};
		Intent communicateIntent = new Intent(
				CommunicateService.ACTION_COMMUNICATE_SERVICE);
		return bindService(communicateIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	private void disconnectService() {
		if (mCommunication != null) {
			try {
				mCommunication.unRegistListenr(mCommunicationListenerExternal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		if (mServiceConnection != null) {
			unbindService(mServiceConnection);
		}
	}

	private OnCommunicationListenerExternal mCommunicationListenerExternal = new OnCommunicationListenerExternal.Stub() {

		@Override
		public void onUserDisconnected(User user) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserConnected(User user) throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceiveMessage(byte[] msg, User sendUser)
				throws RemoteException {
			if (sendUser == null) {
				Log.d(TAG, "User is lost connection.");
				return;
			}
			Message message = mHandler.obtainMessage();
			message.obj = "From " + sendUser.getUserName() + ": "
					+ new String(msg);
			mHandler.sendMessage(message);
		}
	};
	// Communication Service end
}
