package com.vphoainha.itfmobile.gcm;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class Controller {
	private static WakeLock wakeLock;

	// Register this account with the server.
	public static void register(final Context context, final String regId, final int type) {

		Log.i(Config.TAG, "registering device (regId = " + regId + ")");

		GCMRegistrar.setRegisteredOnServer(context, true);
	}

	// Unregister this account/device pair within the server.
	public static void unregister(final Context context, final String regId) {
		Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");
		GCMRegistrar.setRegisteredOnServer(context, false);
	}

	public static void acquireWakeLock(Context context) {
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WakeLock");

		wakeLock.acquire();
	}

	public static void releaseWakeLock() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}

}
