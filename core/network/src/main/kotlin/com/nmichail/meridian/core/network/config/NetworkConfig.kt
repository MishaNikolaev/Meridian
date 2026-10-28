package com.nmichail.meridian.core.network.config

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor() {

	private companion object {
		//TODO Заменить айпишник после миграции на хост
		const val PORT = 8080
		const val EMULATOR_HOST = "10.0.2.2"
		const val DEVICE_HOST = "192.168.0.11"

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

	val host: String = if (isEmulator()) EMULATOR_HOST else DEVICE_HOST

	val port: Int = PORT

	val baseUrl: String = "http://$host:$port/"
}
