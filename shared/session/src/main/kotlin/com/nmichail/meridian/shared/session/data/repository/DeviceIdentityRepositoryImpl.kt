package com.nmichail.meridian.shared.session.data.repository

import android.os.Build
import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DeviceIdentityStore
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.shared.session.data.datasource.DeviceIdentityDataSource
import javax.inject.Inject

class DeviceIdentityRepositoryImpl @Inject constructor(
	private val dataSource: DeviceIdentityDataSource,
) : DeviceIdentityStore {

	private companion object {

		const val FALLBACK_NAME = "Android"
		const val NAME_LIMIT = 128
	}

	override fun identity(): DeviceIdentity = synchronized(this) {
		dataSource.read() ?: DeviceIdentity.generate().also(dataSource::write)
	}

	override fun preKeys(): DevicePreKeys = synchronized(this) {
		val identity = identity()
		dataSource.readPreKeys() ?: identity.createPreKeys().also(dataSource::writePreKeys)
	}

	override fun reset() = synchronized(this) {
		dataSource.clear()
	}

	override fun deviceName(): String {
		val model = Build.MODEL.trim()
		val name = if (model.isEmpty()) FALLBACK_NAME else model
		return name.take(NAME_LIMIT)
	}
}
