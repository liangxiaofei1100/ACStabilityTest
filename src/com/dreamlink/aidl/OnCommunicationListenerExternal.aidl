package com.dreamlink.aidl;
import com.dreamlink.aidl.User;
interface OnCommunicationListenerExternal{
		void onReceiveMessage(in byte[] msg,in User sendUser);
		void onUserConnected(in User user);
		void onUserDisconnected(in User user);
}