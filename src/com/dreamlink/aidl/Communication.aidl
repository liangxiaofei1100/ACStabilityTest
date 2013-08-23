package com.dreamlink.aidl;

import com.dreamlink.aidl.OnCommunicationListenerExternal;
import com.dreamlink.aidl.User;
interface Communication{
	void registListenr(OnCommunicationListenerExternal lis,int appid);
	void sendMessage(in byte[] msg,int appID,in User user);
	List<User> getAllUser();
	void unRegistListenr(OnCommunicationListenerExternal lis);
	User getLocalUser();
	User setLocalUser();
}