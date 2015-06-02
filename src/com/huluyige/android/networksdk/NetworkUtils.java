package com.huluyige.android.networksdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkUtils {

	public static NetworkInfo getCurrentActiveNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}

	public static void setwifiEnabled(Context context, boolean enabled) {
		WifiManager wifiman = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wifiman.setWifiEnabled(enabled);
	}

	public static boolean isWifiEnabled(Context context) {
		WifiManager wifiman = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifiman.isWifiEnabled();
	}
}
