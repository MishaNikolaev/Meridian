package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKey
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.storage.LocalPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeviceIdentityDataSourceImplTest {

	private val localPreferences: LocalPreferences = mock()
	private val dataSource = DeviceIdentityDataSourceImpl(localPreferences)
	private val identity: DeviceIdentity = mock()
	private val preKeys = DevicePreKeys(
		signedPreKey = DevicePreKey(id = 1, publicKey = "signed-public", privateKey = "signed-private"),
		signedPreKeySignature = "signature",
		oneTimePreKey = DevicePreKey(id = 2, publicKey = "once-public", privateKey = "once-private"),
	)

	@Test
	fun `missing device id EXPECT no identity`() {
		whenever(localPreferences.getString("device_identity_id")) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `missing public key EXPECT no identity`() {
		whenever(localPreferences.getString("device_identity_id")) doReturn "device"
		whenever(localPreferences.getString("device_identity_public")) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `missing private key EXPECT no identity`() {
		whenever(localPreferences.getString("device_identity_id")) doReturn "device"
		whenever(localPreferences.getString("device_identity_public")) doReturn "public"
		whenever(localPreferences.getSecret("device_identity_private")) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `write EXPECT device id`() {
		stubIdentity()

		dataSource.write(identity)

		verify(localPreferences).putString("device_identity_id", "device")
	}

	@Test
	fun `write EXPECT public key`() {
		stubIdentity()

		dataSource.write(identity)

		verify(localPreferences).putString("device_identity_public", "public")
	}

	@Test
	fun `write EXPECT private key`() {
		stubIdentity()

		dataSource.write(identity)

		verify(localPreferences).putSecret("device_identity_private", "private")
	}

	@Test
	fun `missing signed prekey EXPECT no prekeys`() {
		whenever(localPreferences.getString("device_signed_prekey_id")) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `unreadable signed prekey id EXPECT no prekeys`() {
		whenever(localPreferences.getString("device_signed_prekey_id")) doReturn "no"

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signed public key EXPECT no prekeys`() {
		whenever(localPreferences.getString("device_signed_prekey_id")) doReturn "1"
		whenever(localPreferences.getString("device_signed_prekey_public")) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signed private key EXPECT no prekeys`() {
		whenever(localPreferences.getString("device_signed_prekey_id")) doReturn "1"
		whenever(localPreferences.getString("device_signed_prekey_public")) doReturn "signed-public"
		whenever(localPreferences.getSecret("device_signed_prekey_private")) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signature EXPECT no prekeys`() {
		stubSignedPreKey()
		whenever(localPreferences.getString("device_signed_prekey_signature")) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing one time prekey EXPECT no prekeys`() {
		stubSignedPreKey()
		whenever(localPreferences.getString("device_signed_prekey_signature")) doReturn "signature"
		whenever(localPreferences.getString("device_onetime_prekey_id")) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `write prekeys EXPECT signed prekey id`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putString("device_signed_prekey_id", "1")
	}

	@Test
	fun `write prekeys EXPECT signed public key`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putString("device_signed_prekey_public", "signed-public")
	}

	@Test
	fun `write prekeys EXPECT signed private key`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putSecret("device_signed_prekey_private", "signed-private")
	}

	@Test
	fun `write prekeys EXPECT signature`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putString("device_signed_prekey_signature", "signature")
	}

	@Test
	fun `write prekeys EXPECT one time prekey id`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putString("device_onetime_prekey_id", "2")
	}

	@Test
	fun `write prekeys EXPECT one time public key`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putString("device_onetime_prekey_public", "once-public")
	}

	@Test
	fun `write prekeys EXPECT one time private key`() {
		dataSource.writePreKeys(preKeys)

		verify(localPreferences).putSecret("device_onetime_prekey_private", "once-private")
	}

	@Test
	fun `clear EXPECT removed device id`() {
		dataSource.clear()

		verify(localPreferences).remove("device_identity_id")
	}

	@Test
	fun `clear EXPECT removed public key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_identity_public")
	}

	@Test
	fun `clear EXPECT removed private key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_identity_private")
	}

	@Test
	fun `clear EXPECT removed signed prekey id`() {
		dataSource.clear()

		verify(localPreferences).remove("device_signed_prekey_id")
	}

	@Test
	fun `clear EXPECT removed signed public key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_signed_prekey_public")
	}

	@Test
	fun `clear EXPECT removed signed private key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_signed_prekey_private")
	}

	@Test
	fun `clear EXPECT removed signature`() {
		dataSource.clear()

		verify(localPreferences).remove("device_signed_prekey_signature")
	}

	@Test
	fun `clear EXPECT removed one time prekey id`() {
		dataSource.clear()

		verify(localPreferences).remove("device_onetime_prekey_id")
	}

	@Test
	fun `clear EXPECT removed one time public key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_onetime_prekey_public")
	}

	@Test
	fun `clear EXPECT removed one time private key`() {
		dataSource.clear()

		verify(localPreferences).remove("device_onetime_prekey_private")
	}

	private fun stubIdentity() {
		whenever(identity.deviceId) doReturn "device"
		whenever(identity.publicKey) doReturn "public"
		whenever(identity.exportPrivateKey()) doReturn "private"
	}

	private fun stubSignedPreKey() {
		whenever(localPreferences.getString("device_signed_prekey_id")) doReturn "1"
		whenever(localPreferences.getString("device_signed_prekey_public")) doReturn "signed-public"
		whenever(localPreferences.getSecret("device_signed_prekey_private")) doReturn "signed-private"
	}
}
