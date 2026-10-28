package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.shared.session.data.datasource.SessionLocalDataSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SessionRepositoryImplTest {

	private val dataSource: SessionLocalDataSource = mock()
	private val repository = SessionRepositoryImpl(dataSource)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `is signed in EXPECT data source value`() {
		whenever(dataSource.isSignedIn()) doReturn true

		val actual = repository.isSignedIn()

		assertEquals(true, actual)
	}

	@Test
	fun `get access token EXPECT data source token`() {
		whenever(dataSource.getAccessToken()) doReturn "access"

		val actual = repository.getAccessToken()

		assertEquals("access", actual)
	}

	@Test
	fun `get refresh token EXPECT data source token`() {
		whenever(dataSource.getRefreshToken()) doReturn "refresh"

		val actual = repository.getRefreshToken()

		assertEquals("refresh", actual)
	}

	@Test
	fun `get user id EXPECT data source id`() {
		whenever(dataSource.getUserId()) doReturn "user"

		val actual = repository.getUserId()

		assertEquals("user", actual)
	}

	@Test
	fun `get device id EXPECT data source id`() {
		whenever(dataSource.getDeviceId()) doReturn "device"

		val actual = repository.getDeviceId()

		assertEquals("device", actual)
	}

	@Test
	fun `save host EXPECT data source host`() {
		repository.saveHost("meridian.name.ru")

		verify(dataSource).saveHost("meridian.name.ru")
	}

	@Test
	fun `save EXPECT data source save`() {
		repository.save(session)

		verify(dataSource).save(session)
	}

	@Test
	fun `clear EXPECT data source clear`() {
		repository.clear()

		verify(dataSource).clear()
	}
}
