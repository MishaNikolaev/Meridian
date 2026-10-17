package com.nmichail.meridian.core.network.config

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor() {

	private companion object {

		const val EMULATOR_GATEWAY = "http://10.0.2.2:8080/"
		const val DEVICE_GATEWAY = "http://127.0.0.1:8080/"

		fun isEmulator(): Boolean {
			val fingerprint = Build.FINGERPRINT
			return fingerprint.startsWith("generic")
				|| fingerprint.startsWith("unknown")
				|| fingerprint.contains("emulator")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for")
				|| Build.HARDWARE.contains("goldfish")
				|| Build.HARDWARE.contains("ranchu")
		}
	}

	val baseUrl: String = if (isEmulator()) EMULATOR_GATEWAY else DEVICE_GATEWAY
}
