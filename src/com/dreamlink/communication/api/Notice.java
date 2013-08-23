package com.dreamlink.communication.api;

import android.content.Context;
import android.widget.Toast;

/**
 * Show Android toast.
 * 
 */
public class Notice {
	private static final String TAG = "Notice";

	private Toast toast;
	private Context context;

	public Notice(Context context) {
		this.context = context;
	}

	public void showToast(String mensagem) {
		try {
			if (toast != null) {
				toast.setText(mensagem);
			} else {
				toast = Toast.makeText(context, mensagem, Toast.LENGTH_LONG);
			}
			toast.show();
		} catch (Exception e) {
			Log.e(TAG, "showToast error, " + e);
		}
	}

	public void closeToast() {
		if (toast != null)
			toast.cancel();
	}

}