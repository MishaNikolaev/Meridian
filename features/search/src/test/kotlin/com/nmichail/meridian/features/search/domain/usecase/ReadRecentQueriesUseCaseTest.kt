package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ReadRecentQueriesUseCaseTest {

	private val repository: RecentQueryRepository = mock()
	private val useCase = ReadRecentQueriesUseCase(repository)

	@Test
	fun `empty store EXPECT no queries`() {
		whenever(repository.read()) doReturn null

		assertEquals(emptyList<String>(), useCase())
	}

	@Test
	fun `stored lines EXPECT queries`() {
		whenever(repository.read()) doReturn "пуши\nВласов"

		assertEquals(listOf("пуши", "Власов"), useCase())
	}
}
