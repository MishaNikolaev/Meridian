package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.shared.session.data.datasource.DeviceIdentityDataSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeviceIdentityRepositoryImplTest {

	private val dataSource: DeviceIdentityDataSource = mock()
	private val repository = DeviceIdentityRepositoryImpl(dataSource)
	private val identity: DeviceIdentity = mock()
	private val preKeys: DevicePreKeys = mock()

	@Test
	fun `stored identity EXPECT that identity`() {
		whenever(dataSource.read()) doReturn identity

		val actual = repository.getIdentity()

		assertEquals(identity, actual)
	}

	@Test
	fun `stored prekeys EXPECT those prekeys`() {
		whenever(dataSource.read()) doReturn identity
		whenever(dataSource.readPreKeys()) doReturn preKeys

		val actual = repository.getPreKeys()

		assertEquals(preKeys, actual)
	}

	@Test
	fun `reset EXPECT cleared storage`() {
		repository.reset()

		verify(dataSource).clear()
	}
}
