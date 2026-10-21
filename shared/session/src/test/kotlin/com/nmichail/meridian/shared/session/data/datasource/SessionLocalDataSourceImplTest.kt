package com.nmichail.meridian.shared.session.data.datasource

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.storage.LocalPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SessionLocalDataSourceImplTest {

	private val localPreferences: LocalPreferences = mock()
	private val dataSource = SessionLocalDataSourceImpl(localPreferences)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `fresh session EXPECT signed in`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(localPreferences.getSecret("session_access_token")) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(true, actual)
	}

	@Test
	fun `missing expiry EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn null

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `unreadable expiry EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn "soon"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `expired session EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn "0"
		whenever(localPreferences.getSecret("session_access_token")) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `session inside skew EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn (System.currentTimeMillis() + 29_000).toString()
		whenever(localPreferences.getSecret("session_access_token")) doReturn "access"

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `blank access token EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(localPreferences.getSecret("session_access_token")) doReturn " "

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `missing access token EXPECT signed out`() {
		whenever(localPreferences.getString("session_expires_at")) doReturn (System.currentTimeMillis() + 120_000).toString()
		whenever(localPreferences.getSecret("session_access_token")) doReturn null

		val actual = dataSource.isSignedIn()

		assertEquals(false, actual)
	}

	@Test
	fun `stored access token EXPECT token`() {
		whenever(localPreferences.getSecret("session_access_token")) doReturn "access"

		val actual = dataSource.getAccessToken()

		assertEquals("access", actual)
	}

	@Test
	fun `blank access token EXPECT no token`() {
		whenever(localPreferences.getSecret("session_access_token")) doReturn " "

		val actual = dataSource.getAccessToken()

		assertEquals(null, actual)
	}

	@Test
	fun `missing access token EXPECT no token`() {
		whenever(localPreferences.getSecret("session_access_token")) doReturn null

		val actual = dataSource.getAccessToken()

		assertEquals(null, actual)
	}

	@Test
	fun `stored refresh token EXPECT token`() {
		whenever(localPreferences.getSecret("session_refresh_token")) doReturn "refresh"

		val actual = dataSource.getRefreshToken()

		assertEquals("refresh", actual)
	}

	@Test
	fun `blank refresh token EXPECT no token`() {
		whenever(localPreferences.getSecret("session_refresh_token")) doReturn " "

		val actual = dataSource.getRefreshToken()

		assertEquals(null, actual)
	}

	@Test
	fun `missing refresh token EXPECT no token`() {
		whenever(localPreferences.getSecret("session_refresh_token")) doReturn null

		val actual = dataSource.getRefreshToken()

		assertEquals(null, actual)
	}

	@Test
	fun `stored user id EXPECT id`() {
		whenever(localPreferences.getString("session_user_id")) doReturn "user"

		val actual = dataSource.getUserId()

		assertEquals("user", actual)
	}

	@Test
	fun `blank user id EXPECT no id`() {
		whenever(localPreferences.getString("session_user_id")) doReturn " "

		val actual = dataSource.getUserId()

		assertEquals(null, actual)
	}

	@Test
	fun `missing user id EXPECT no id`() {
		whenever(localPreferences.getString("session_user_id")) doReturn null

		val actual = dataSource.getUserId()

		assertEquals(null, actual)
	}

	@Test
	fun `stored device id EXPECT id`() {
		whenever(localPreferences.getString("session_device_id")) doReturn "device"

		val actual = dataSource.getDeviceId()

		assertEquals("device", actual)
	}

	@Test
	fun `blank device id EXPECT no id`() {
		whenever(localPreferences.getString("session_device_id")) doReturn " "

		val actual = dataSource.getDeviceId()

		assertEquals(null, actual)
	}

	@Test
	fun `missing device id EXPECT no id`() {
		whenever(localPreferences.getString("session_device_id")) doReturn null

		val actual = dataSource.getDeviceId()

		assertEquals(null, actual)
	}

	@Test
	fun `save EXPECT user id`() {
		dataSource.save(session)

		verify(localPreferences).putString("session_user_id", "user")
	}

	@Test
	fun `save EXPECT device id`() {
		dataSource.save(session)

		verify(localPreferences).putString("session_device_id", "device")
	}

	@Test
	fun `save EXPECT access token`() {
		dataSource.save(session)

		verify(localPreferences).putSecret("session_access_token", "access")
	}

	@Test
	fun `save EXPECT refresh token`() {
		dataSource.save(session)

		verify(localPreferences).putSecret("session_refresh_token", "refresh")
	}

	@Test
	fun `save EXPECT expiry`() {
		val started = System.currentTimeMillis()

		dataSource.save(session)

		verify(localPreferences).putString(
			eq("session_expires_at"),
			argThat { expiresAt ->
				val savedAt = expiresAt.toLong()
				savedAt >= started + session.expiresIn * 1000 &&
					savedAt <= System.currentTimeMillis() + session.expiresIn * 1000
			},
		)
	}

	@Test
	fun `clear EXPECT removed user id`() {
		dataSource.clear()

		verify(localPreferences).remove("session_user_id")
	}

	@Test
	fun `clear EXPECT removed device id`() {
		dataSource.clear()

		verify(localPreferences).remove("session_device_id")
	}

	@Test
	fun `clear EXPECT removed access token`() {
		dataSource.clear()

		verify(localPreferences).remove("session_access_token")
	}

	@Test
	fun `clear EXPECT removed refresh token`() {
		dataSource.clear()

		verify(localPreferences).remove("session_refresh_token")
	}

	@Test
	fun `clear EXPECT removed expiry`() {
		dataSource.clear()

		verify(localPreferences).remove("session_expires_at")
	}

	@Test
	fun `clear EXPECT removed expiry seconds`() {
		dataSource.clear()

		verify(localPreferences).remove("session_expires_in")
	}
}
