package org.elastos.elastoscarriermultiinstancetest;

import android.util.Log;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;
import org.elastos.carrier.UserInfo;

import java.util.List;

public class CarrierHandler implements org.elastos.carrier.CarrierHandler{
	private final String TAG = "CarrierHandler" ;
	@Override
	public void onIdle(Carrier carrier) {
//		Log.d(TAG ,carrier.hashCode() + " - onIdle");
	}

	@Override
	public void onConnection(Carrier carrier, ConnectionStatus status) {
		Log.d(TAG ,carrier.hashCode() + " - onConnection");

	}

	@Override
	public void onReady(Carrier carrier) {
		Log.d(TAG ,carrier.hashCode() + " - onReady");
	}

	@Override
	public void onSelfInfoChanged(Carrier carrier, UserInfo userInfo) {
		Log.d(TAG ,carrier.hashCode() + " - onSelfInfoChanged");
	}

	@Override
	public void onFriends(Carrier carrier, List<FriendInfo> friends) {
		Log.d(TAG ,carrier.hashCode() + " - onFriends");
	}

	@Override
	public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendConnection");
	}

	@Override
	public void onFriendInfoChanged(Carrier carrier, String friendId, FriendInfo info) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendInfoChanged");
	}

	@Override
	public void onFriendPresence(Carrier carrier, String friendId, PresenceStatus presence) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendPresence");
	}

	@Override
	public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendRequest");
	}

	@Override
	public void onFriendAdded(Carrier carrier, FriendInfo friendInfo) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendAdded");
	}

	@Override
	public void onFriendRemoved(Carrier carrier, String friendId) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendRemoved");
	}

	@Override
	public void onFriendMessage(Carrier carrier, String from, byte[] message, boolean isOffline) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendMessage");
	}

	@Override
	public void onFriendInviteRequest(Carrier carrier, String from, String data) {
		Log.d(TAG ,carrier.hashCode() + " - onFriendInviteRequest");
	}

	@Override
	public void onGroupInvite(Carrier carrier, String from, byte[] cookie) {
		Log.d(TAG ,carrier.hashCode() + " - onGroupInvite");
	}
}
