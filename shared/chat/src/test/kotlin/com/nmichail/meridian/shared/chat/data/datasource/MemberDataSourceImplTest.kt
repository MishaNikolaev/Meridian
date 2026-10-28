package com.nmichail.meridian.shared.chat.data.datasource

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.dto.KeyDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MemberDataSourceImplTest {

	private val api: ChatApi = mock()
	private val dataSource = MemberDataSourceImpl(api)

	@Test
	fun `users EXPECT api users`() = runTest {
		val users = listOf(
			UserCardDto(
				userId = "user",
				username = "anna",
				displayName = "Анна",
			),
		)
		whenever(api.getUsers("anna")) doReturn users

		val actual = dataSource.getUsers("anna")

		assertEquals(users, actual)
	}

	@Test
	fun `keys EXPECT api keys`() = runTest {
		val keys = KeyDto(
			userId = "user",
			devices = emptyList(),
		)
		whenever(api.getKeys("user")) doReturn keys

		val actual = dataSource.getKeys("user")

		assertEquals(keys, actual)
	}
}