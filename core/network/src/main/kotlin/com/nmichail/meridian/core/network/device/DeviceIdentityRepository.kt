package com.nmichail.meridian.core.network.device

interface DeviceIdentityRepository {

	fun getIdentity(): DeviceIdentity

	fun getPreKeys(): DevicePreKeys

	fun getDeviceName(): String

	fun reset()
}
