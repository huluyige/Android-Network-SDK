package com.huluyige.android.networksdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "NetworkBroadcastReceiver";
	private NetworkChangeListener listener;
	private static String currentSsid;
	private static String currentBssid;
	private static String currentGateway;
	private static String currentMobileAPN;

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		Log.i(TAG, "onReceive() NetworkInfo " + networkInfo);
		if (networkInfo == null) {
			networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo == null) {
				return;
			}
		}

		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			processWifiNetworkInfo(context, networkInfo);
			return;
		}

		processNetworkInfo(context, networkInfo);
	}

	private void processNetworkInfo(Context context, NetworkInfo networkInfo) {
		int netType = networkInfo.getType();
		int netSubtype = networkInfo.getSubtype();
		switch (netType) {
		case ConnectivityManager.TYPE_WIFI:
			processWifiNetworkInfo(context, networkInfo);
			break;
		case ConnectivityManager.TYPE_MOBILE:
			if (!networkInfo.isRoaming()) {
				Log.i(TAG, "mobile network subtype: " + netSubtype);
				processMobileNetworkInfo(context, networkInfo);
			}
			break;
		}
	}

	private void processWifiNetworkInfo(Context context, NetworkInfo networkInfo) {
		WifiManager wifiMgr = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		switch (networkInfo.getDetailedState()) {
		case CONNECTED:
			Log.i(TAG, "onReceive() signaled CONNECTED");
			// the OS has switched from 3G to Wifi, send a disconnect from 3g
			// before sending wifi connect event
			if (currentMobileAPN != null) {
				currentMobileAPN = null;
				if (listener != null) {
					listener.onNetworkChangeListener(NetworkType.Mobile,
							NetworkEvent.Disconnected, null);
				}
			}

			if (currentSsid == null && currentBssid == null) {
				if (wifiInfo.getSSID() != null && wifiInfo.getBSSID() != null) {
					currentSsid = wifiInfo.getSSID();
					currentBssid = wifiInfo.getBSSID();
					currentGateway = getStringIp(wifiMgr.getDhcpInfo().gateway);
					handleWifiConnectionState(context, NetworkEvent.Connected);
				}
			} else {
				if (wifiInfo.getSSID() != null && wifiInfo.getBSSID() != null) {
					if (!wifiInfo.getSSID().equals(currentSsid)
							&& !(wifiInfo.getBSSID().equals(currentBssid))) {
						handleWifiConnectionState(context,
								NetworkEvent.Disconnected);
						currentSsid = wifiInfo.getSSID();
						currentBssid = wifiInfo.getBSSID();
						currentGateway = getStringIp(wifiMgr.getDhcpInfo().gateway);
						handleWifiConnectionState(context,
								NetworkEvent.Connected);
					}
				}
			}
			break;
		case DISCONNECTED:
			Log.i(TAG, "onReceive() signaled DISCONNECTED");
			handleWifiConnectionState(context, NetworkEvent.Disconnected);
			currentSsid = null;
			currentBssid = null;
			currentGateway = null;
			break;
		case AUTHENTICATING:
		case CONNECTING:
		case OBTAINING_IPADDR:
		case DISCONNECTING:
			if (listener != null) {
				Bundle networkDetails = new Bundle();
				networkDetails.putString(NetworkDetailKey.Ssid.name(),
						currentSsid);
				networkDetails.putString(NetworkDetailKey.Gateway.name(),
						currentGateway);
				listener.onNetworkChangeListener(NetworkType.WiFi,
						NetworkEvent.InTransition, networkDetails);
			}
			break;
		default:
			break;
		}

	}

	private void processMobileNetworkInfo(Context context,
			NetworkInfo networkInfo) {
		Log.i(TAG, "Mobile" + networkInfo.getDetailedState());
		switch (networkInfo.getDetailedState()) {
		case CONNECTED:
			if (currentMobileAPN == null) {
				currentMobileAPN = networkInfo.getExtraInfo();
				if (listener != null) {
					Bundle networkDetails = new Bundle();
					networkDetails.putString(NetworkDetailKey.mobileAPN.name(),
							currentMobileAPN);
					listener.onNetworkChangeListener(NetworkType.Mobile,
							NetworkEvent.Connected, networkDetails);
				}
			}
			break;
		case DISCONNECTED:
			// the mobile disconnect event needs to be sent only when the device
			// is not connected to wifi
			if (listener != null && currentSsid == null) {
				currentMobileAPN = null;
				listener.onNetworkChangeListener(NetworkType.Mobile,
						NetworkEvent.Disconnected, null);
			}
			break;
		default:
			break;
		}

	}

	private void handleWifiConnectionState(Context context,
			NetworkEvent networkEvent) {
		if (listener != null) {
			Bundle networkDetails = new Bundle();
			networkDetails.putString(NetworkDetailKey.Ssid.name(), currentSsid);
			networkDetails.putString(NetworkDetailKey.Gateway.name(),
					currentGateway);
			listener.onNetworkChangeListener(NetworkType.WiFi, networkEvent,
					networkDetails);
		}
	}

	public static void setDetailsObtainedIndirectly(String ssid, String bssid) {
		currentSsid = ssid;
		currentBssid = bssid;
	}

	public static String getMacAddress() {
		return currentBssid;
	}

	private String getStringIp(int ipAddress) {
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	public void setNetworkChangeListener(NetworkChangeListener listener) {
		this.listener = listener;
	}

	public interface NetworkChangeListener {
		void onNetworkChangeListener(NetworkType networkType,
				NetworkEvent networkEvent, Bundle networkDetails);
	}
}
