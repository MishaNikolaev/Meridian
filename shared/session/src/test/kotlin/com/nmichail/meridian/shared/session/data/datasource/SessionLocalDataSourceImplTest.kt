package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.storage.SecretCipher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SessionLocalDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private val secrets: SecretCipher = mock()
	private lateinit var dataSource: SessionLocalDataSourceImpl
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		whenever(editor.remove(any())) doReturn editor
		whenever(secrets.isSealed(any())) doReturn true
		whenever(secrets.open(any())) doAnswer { it.getArgument<String>(0) }
		whenever(secrets.seal(any())) doAnswer { it.getArgument<String>(0) }
		dataSource = SessionLocalDataSourceImpl(context, secrets)
	}

	@Test
	fun `fresh session EXPECT signed in`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(preferences.getString("session_access_token", null)) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(true, actual)
	}

	@Test
	fun `missing expiry EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn null

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `unreadable expiry EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn "soon"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `expired session EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn "0"
		whenever(preferences.getString("session_access_token", null)) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `session inside skew EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn (System.currentTimeMillis() + 29_000).toString()
		whenever(preferences.getString("session_access_token", null)) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `blank access token EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(preferences.getString("session_access_token", null)) doReturn " "

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `missing access token EXPECT signed out`() {
		whenever(preferences.getString("session_expires_at", null)) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(preferences.getString("session_access_token", null)) doReturn null

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `stored access token EXPECT token`() {
		whenever(preferences.getString("session_access_token", null)) doReturn "access"

		val actual = dataSource.getAccessToken()

		assertEquals("access", actual)
	}

	@Test
	fun `blank access token EXPECT no token`() {
		whenever(preferences.getString("session_access_token", null)) doReturn " "

		val actual = dataSource.getAccessToken()

		assertEquals(null, actual)
	}

	@Test
	fun `missing access token EXPECT no token`() {
		whenever(preferences.getString("session_access_token", null)) doReturn null

		val actual = dataSource.getAccessToken()

		assertEquals(null, actual)
	}

	@Test
	fun `stored refresh token EXPECT token`() {
		whenever(preferences.getString("session_refresh_token", null)) doReturn "refresh"

		val actual = dataSource.getRefreshToken()

		assertEquals("refresh", actual)
	}

	@Test
	fun `blank refresh token EXPECT no token`() {
		whenever(preferences.getString("session_refresh_token", null)) doReturn " "

		val actual = dataSource.getRefreshToken()

		assertEquals(null, actual)
	}

	@Test
	fun `missing refresh token EXPECT no token`() {
		whenever(preferences.getString("session_refresh_token", null)) doReturn null

		val actual = dataSource.getRefreshToken()

		assertEquals(null, actual)
	}

	@Test
	fun `stored user id EXPECT id`() {
		whenever(preferences.getString("session_user_id", null)) doReturn "user"

		val actual = dataSource.getUserId()

		assertEquals("user", actual)
	}

	@Test
	fun `blank user id EXPECT no id`() {
		whenever(preferences.getString("session_user_id", null)) doReturn " "

		val actual = dataSource.getUserId()

		assertEquals(null, actual)
	}

	@Test
	fun `missing user id EXPECT no id`() {
		whenever(preferences.getString("session_user_id", null)) doReturn null

		val actual = dataSource.getUserId()

		assertEquals(null, actual)
	}

	@Test
	fun `stored device id EXPECT id`() {
		whenever(preferences.getString("session_device_id", null)) doReturn "device"

		val actual = dataSource.getDeviceId()

		assertEquals("device", actual)
	}

	@Test
	fun `blank device id EXPECT no id`() {
		whenever(preferences.getString("session_device_id", null)) doReturn " "

		val actual = dataSource.getDeviceId()

		assertEquals(null, actual)
	}

	@Test
	fun `missing device id EXPECT no id`() {
		whenever(preferences.getString("session_device_id", null)) doReturn null

		val actual = dataSource.getDeviceId()

		assertEquals(null, actual)
	}

	@Test
	fun `save EXPECT user id`() {
		dataSource.save(session)

		verify(editor).putString("session_user_id", "user")
	}

	@Test
	fun `save EXPECT device id`() {
		dataSource.save(session)

		verify(editor).putString("session_device_id", "device")
	}

	@Test
	fun `save EXPECT access token`() {
		dataSource.save(session)

		verify(editor).putString("session_access_token", "access")
	}

	@Test
	fun `save EXPECT refresh token`() {
		dataSource.save(session)

		verify(editor).putString("session_refresh_token", "refresh")
	}

	@Test
	fun `save EXPECT expiry`() {
		val started = System.currentTimeMillis()

		dataSource.save(session)

		verify(editor).putString(
			eq("session_expires_at"),
			argThat { expiresAt ->
				val savedAt = expiresAt.toLong()
				savedAt >= started + session.expiresIn * 1000 &&
					savedAt <= System.currentTimeMillis() + session.expiresIn * 1000
			},
		)
	}

	@Test
	fun `save host EXPECT stored host`() {
		dataSource.saveHost(" meridian.name.ru ")

		verify(editor).putString("session_host", "meridian.name.ru")
	}

	@Test
	fun `clear EXPECT removed host`() {
		dataSource.clear()

		verify(editor).remove("session_host")
	}

	@Test
	fun `clear EXPECT removed user id`() {
		dataSource.clear()

		verify(editor).remove("session_user_id")
	}

	@Test
	fun `clear EXPECT removed device id`() {
		dataSource.clear()

		verify(editor).remove("session_device_id")
	}

	@Test
	fun `clear EXPECT removed access token`() {
		dataSource.clear()

		verify(editor).remove("session_access_token")
	}

	@Test
	fun `clear EXPECT removed refresh token`() {
		dataSource.clear()

		verify(editor).remove("session_refresh_token")
	}

	@Test
	fun `clear EXPECT removed expiry`() {
		dataSource.clear()

		verify(editor).remove("session_expires_at")
	}

	@Test
	fun `clear EXPECT removed expiry seconds`() {
		dataSource.clear()

		verify(editor).remove("session_expires_in")
	}
}
