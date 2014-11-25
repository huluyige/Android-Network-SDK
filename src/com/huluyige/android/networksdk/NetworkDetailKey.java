package com.huluyige.android.networksdk;

public enum NetworkDetailKey {
	/**
	 * WiFi only. The SSID of the WiFi network for which we receive the
	 * NetworkEvent
	 */
	Ssid,

	/**
	 * WiFi only. The BSSID of the WiFi network for which we receive the
	 * NetworkEvent
	 */
	Bssid,

	/**
	 * WiFi only. The GATEWAY of the WiFi network for which we receive the
	 * NetworkEvent
	 */
	Gateway,

	/**
	 * Mobile only. The access point name of the mobile network for which we
	 * receive the NetworkEvent
	 */
	mobileAPN
}
