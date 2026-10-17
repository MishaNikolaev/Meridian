package com.nmichail.meridian.core.network.device

interface DeviceIdentityStore {

	fun identity(): DeviceIdentity

	fun preKeys(): DevicePreKeys

	fun deviceName(): String

	fun reset()
}
