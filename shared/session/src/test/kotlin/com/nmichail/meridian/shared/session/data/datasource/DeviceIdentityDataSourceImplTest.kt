package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKey
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.storage.SecretCipher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeviceIdentityDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private val secrets: SecretCipher = mock()
	private lateinit var dataSource: DeviceIdentityDataSourceImpl
	private val identity: DeviceIdentity = mock()
	private val preKeys = DevicePreKeys(
		signedPreKey = DevicePreKey(id = 1, publicKey = "signed-public", privateKey = "signed-private"),
		signedPreKeySignature = "signature",
		oneTimePreKey = DevicePreKey(id = 2, publicKey = "once-public", privateKey = "once-private"),
	)

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		whenever(editor.remove(any())) doReturn editor
		whenever(secrets.isSealed(any())) doReturn true
		whenever(secrets.isCurrent(any())) doReturn true
		whenever(secrets.open(any())) doAnswer { it.getArgument<String>(0) }
		whenever(secrets.seal(any())) doAnswer { it.getArgument<String>(0) }
		dataSource = DeviceIdentityDataSourceImpl(context, secrets)
	}

	@Test
	fun `missing device id EXPECT no identity`() {
		whenever(preferences.getString("device_identity_id", null)) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `missing public key EXPECT no identity`() {
		whenever(preferences.getString("device_identity_id", null)) doReturn "device"
		whenever(preferences.getString("device_identity_public", null)) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `missing private key EXPECT no identity`() {
		whenever(preferences.getString("device_identity_id", null)) doReturn "device"
		whenever(preferences.getString("device_identity_public", null)) doReturn "public"
		whenever(preferences.getString("device_identity_private", null)) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `write EXPECT device id`() {
		stubIdentity()

		dataSource.write(identity)

		verify(editor).putString("device_identity_id", "device")
	}

	@Test
	fun `write EXPECT public key`() {
		stubIdentity()

		dataSource.write(identity)

		verify(editor).putString("device_identity_public", "public")
	}

	@Test
	fun `write EXPECT private key`() {
		stubIdentity()

		dataSource.write(identity)

		verify(editor).putString("device_identity_private", "private")
	}

	@Test
	fun `missing signed prekey EXPECT no prekeys`() {
		whenever(preferences.getString("device_signed_prekey_id", null)) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `unreadable signed prekey id EXPECT no prekeys`() {
		whenever(preferences.getString("device_signed_prekey_id", null)) doReturn "no"

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signed public key EXPECT no prekeys`() {
		whenever(preferences.getString("device_signed_prekey_id", null)) doReturn "1"
		whenever(preferences.getString("device_signed_prekey_public", null)) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signed private key EXPECT no prekeys`() {
		whenever(preferences.getString("device_signed_prekey_id", null)) doReturn "1"
		whenever(preferences.getString("device_signed_prekey_public", null)) doReturn "signed-public"
		whenever(preferences.getString("device_signed_prekey_private", null)) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing signature EXPECT no prekeys`() {
		stubSignedPreKey()
		whenever(preferences.getString("device_signed_prekey_signature", null)) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `missing one time prekey EXPECT no prekeys`() {
		stubSignedPreKey()
		whenever(preferences.getString("device_signed_prekey_signature", null)) doReturn "signature"
		whenever(preferences.getString("device_onetime_prekey_id", null)) doReturn null

		val actual = dataSource.readPreKeys()

		assertEquals(null, actual)
	}

	@Test
	fun `write prekeys EXPECT signed prekey id`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_signed_prekey_id", "1")
	}

	@Test
	fun `write prekeys EXPECT signed public key`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_signed_prekey_public", "signed-public")
	}

	@Test
	fun `write prekeys EXPECT signed private key`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_signed_prekey_private", "signed-private")
	}

	@Test
	fun `write prekeys EXPECT signature`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_signed_prekey_signature", "signature")
	}

	@Test
	fun `write prekeys EXPECT one time prekey id`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_onetime_prekey_id", "2")
	}

	@Test
	fun `write prekeys EXPECT one time public key`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_onetime_prekey_public", "once-public")
	}

	@Test
	fun `write prekeys EXPECT one time private key`() {
		dataSource.writePreKeys(preKeys)

		verify(editor).putString("device_onetime_prekey_private", "once-private")
	}

	@Test
	fun `clear EXPECT removed device id`() {
		dataSource.clear()

		verify(editor).remove("device_identity_id")
	}

	@Test
	fun `clear EXPECT removed public key`() {
		dataSource.clear()

		verify(editor).remove("device_identity_public")
	}

	@Test
	fun `clear EXPECT removed private key`() {
		dataSource.clear()

		verify(editor).remove("device_identity_private")
	}

	@Test
	fun `clear EXPECT removed signed prekey id`() {
		dataSource.clear()

		verify(editor).remove("device_signed_prekey_id")
	}

	@Test
	fun `clear EXPECT removed signed public key`() {
		dataSource.clear()

		verify(editor).remove("device_signed_prekey_public")
	}

	@Test
	fun `clear EXPECT removed signed private key`() {
		dataSource.clear()

		verify(editor).remove("device_signed_prekey_private")
	}

	@Test
	fun `clear EXPECT removed signature`() {
		dataSource.clear()

		verify(editor).remove("device_signed_prekey_signature")
	}

	@Test
	fun `clear EXPECT removed one time prekey id`() {
		dataSource.clear()

		verify(editor).remove("device_onetime_prekey_id")
	}

	@Test
	fun `clear EXPECT removed one time public key`() {
		dataSource.clear()

		verify(editor).remove("device_onetime_prekey_public")
	}

	@Test
	fun `clear EXPECT removed one time private key`() {
		dataSource.clear()

		verify(editor).remove("device_onetime_prekey_private")
	}

	private fun stubIdentity() {
		whenever(identity.deviceId) doReturn "device"
		whenever(identity.publicKey) doReturn "public"
		whenever(identity.exportPrivateKey()) doReturn "private"
	}

	private fun stubSignedPreKey() {
		whenever(preferences.getString("device_signed_prekey_id", null)) doReturn "1"
		whenever(preferences.getString("device_signed_prekey_public", null)) doReturn "signed-public"
		whenever(preferences.getString("device_signed_prekey_private", null)) doReturn "signed-private"
	}
}
