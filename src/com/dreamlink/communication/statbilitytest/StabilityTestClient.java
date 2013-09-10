package com.dreamlink.communication.statbilitytest;

import java.util.ArrayList;
import java.util.List;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.CommunicationManager.OnCommunicationListener;
import com.dreamlink.communication.lib.CommunicationManager.OnConnectionChangeListener;
import com.dreamlink.communication.lib.util.Notice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * see {@code StabilityTestActivity}.
 * 
 */
public class StabilityTestClient extends Activity implements
		OnConnectionChangeListener, OnCommunicationListener {
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> mData;
	private boolean mStop = false;
	private Notice mNotice;
	private static final String TAG = "StabilityTestClient";

	private static final int SEND_MODE_ALL = 1;
	private static final int SEND_MODE_SINGLE = 2;
	private int mSendMode = SEND_MODE_ALL;
	private User mSendModeSigleReceiver;

	/** Stability test app id */
	private int mAppID = 0;

	private String mTestMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mAppID = intent.getIntExtra(StabilityTestActivity.EXTRA_APP_ID, 0);

		initView();
		mNotice = new Notice(this);

		// connect to communication service.
		mCommunicationManager = new CommunicationManager(this);
		boolean result = mCommunicationManager.connectCommunicatonService(this,
				this, mAppID);
		Log.d(TAG, "Connect communication service resutl = " + result);

	}

	private class TestThread extends Thread {

		@Override
		public void run() {
			Log.d(TAG, "start Test, run");
			int count = 0;
			while (!mStop) {
				Log.d(TAG, "Send message: " + count + ", send mode = "
						+ mSendMode);
				byte[] message = (TimeUtil.getCurrentTime() + "::"
						+ String.valueOf(count) + '\n' + mTestMessage)
						.getBytes();
				switch (mSendMode) {
				case SEND_MODE_ALL:
					sendMessageToAll(message);
					break;
				case SEND_MODE_SINGLE:
					if (mSendModeSigleReceiver != null) {
						sendMessageToSingle(message, mSendModeSigleReceiver);
					}
					break;

				default:
					break;
				}
				count++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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

			if (!messageString.contains(mTestMessage)) {
				Log.e(TAG,
						"Receive message error. length = "
								+ messageString.length() + ". message: "
								+ messageString);
				mNotice.showToast("Received data error, data: " + messageString);
			} else {
				Log.d(TAG,
						"Receive message success. length = "
								+ messageString.length() + ". message: "
								+ messageString);
			}
			if (mData.size() > 100) {
				mData.clear();
			}
			mData.add(messageString);
			mAdapter.notifyDataSetChanged();
		};
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mCommunicationManager.disconnectCommunicationService();
		mStop = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stability_test, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_send_to_all:
			mSendMode = SEND_MODE_ALL;
			mNotice.showToast("Send to all");
			break;
		case R.id.menu_send_to_single:
			mSendMode = SEND_MODE_SINGLE;
			showReceiverChooserMenu(item);

			break;
		case MENU_SEND_TO_SINGLE:
			String receiver = item.getTitle().toString();
			List<User> allUser = mCommunicationManager.getAllUser();
			for (User user : allUser) {
				if (receiver.equals(user.getUserName())) {
					mSendModeSigleReceiver = user;
					// Get the first matched user. If there are users with the
					// same name, ignore.
					break;
				}
			}
			mNotice.showToast("Send to ID = "
					+ mSendModeSigleReceiver.getUserID() + ", name = "
					+ mSendModeSigleReceiver.getUserName());
			break;
		case R.id.menu_message_size:
			showSetMessageSizeDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showSetMessageSizeDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.message_size_dialog, null);
		final EditText editText = (EditText) textEntryView
				.findViewById(R.id.et_message_size);
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setIconAttribute(android.R.attr.alertDialogIcon)
				.setTitle("Message size of send.")
				.setView(textEntryView)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String messageSize = editText.getText()
										.toString();
								if (!TextUtils.isEmpty(messageSize)) {
									setMessageSize(Integer.valueOf(messageSize));
								}
							}

						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();
		dialog.show();
	}

	private void setMessageSize(int sizeOfByte) {
		if (sizeOfByte >= 10 * 1024) {
			mNotice.showToast("Set size fail. Too large size: " + sizeOfByte);
			return;
		}
		StringBuffer stringBuffer = new StringBuffer();
		if (sizeOfByte < 10) {
			for (int i = 0; i < sizeOfByte; i++) {
				stringBuffer.append(i);
			}
		} else {
			for (int i = 0; i < sizeOfByte / 10; i++) {
				stringBuffer.append("1234567890");
			}
		}
		mTestMessage = stringBuffer.toString();
		mNotice.showToast("Set message size success. size = " + sizeOfByte);
	}

	private static final int MENU_SEND_TO_SINGLE = 1;

	private void showReceiverChooserMenu(MenuItem item) {
		SubMenu subMenu = item.getSubMenu();
		subMenu.clear();

		List<User> allUser = mCommunicationManager.getAllUser();

		if (allUser == null) {
			Log.e(TAG, "showReceiverChooserMenu() getallUser fail");
			return;
		}

		User localUser = mCommunicationManager.getLocalUser();
		for (User user : allUser) {
			if (localUser.getUserID() != user.getUserID()) {
				subMenu.add(1, MENU_SEND_TO_SINGLE, 0, user.getUserName());
			}
		}
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

	@Override
	public void onCommunicationDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommunicationConnected() {
		Log.d(TAG, "start Test");
		TestThread testThread = new TestThread();
		testThread.start();
	}
	// Communication Service end
}
