Android-Network-SDK
===================
Permissions: 
--------------
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

Get active network info:
--------------
		NetworkInfo networkInfo = NetworkUtils.getCurrentActiveNetwork(this);

		if (networkInfo == null) {
			Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (networkInfo.getType()) {
		case ConnectivityManager.TYPE_WIFI:
			Toast.makeText(this,
					"WiFi: " + networkInfo.getExtraInfo() + " connected",
					Toast.LENGTH_SHORT).show();
			break;

		case ConnectivityManager.TYPE_MOBILE:
			Toast.makeText(this,
					"Mobile data connected",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

Monitering network change:
--------------
**Broadcast receiver declaration**

        <receiver android:name="com.huluyige.android.networksdk.NetworkBroadcastReceiver" >
            <intent-filter>
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
            </intent-filter>
        </receiver>

        
**Register listener**      

	NetworkBroadcastReceiver.setNetworkChangeListener(this);


**Method implementation**

	@Override
	public void onNetworkChangeListener(NetworkType networkType,
			NetworkEvent networkEvent, Bundle networkDetails) {
		switch (networkType) {
		case WiFi:
			switch (networkEvent) {
			case Connected:
				String ssid = networkDetails.getString(
						NetworkDetailKey.Ssid.name(), null);

				Toast.makeText(this, "WiFI: " + ssid + " connected.",
						Toast.LENGTH_SHORT).show();
				break;
			case Disconnected:
				Toast.makeText(this, "WiFI disconnected.", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
			break;
		case Mobile:
			switch (networkEvent) {
			case Connected:
				String mobileAPN = networkDetails.getString(
						NetworkDetailKey.mobileAPN.name(), null);

				Toast.makeText(this, "Mobile: " + mobileAPN + " connected.",
						Toast.LENGTH_SHORT).show();
				break;
			case Disconnected:
				Toast.makeText(this, "Mobile disconnected.", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
