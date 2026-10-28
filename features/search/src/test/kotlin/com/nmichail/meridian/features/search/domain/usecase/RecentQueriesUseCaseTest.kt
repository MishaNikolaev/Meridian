package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecentQueriesUseCaseTest {

	private val recentQueryRepository: RecentQueryRepository = mock()
	private val useCase = RecentQueriesUseCase(recentQueryRepository)

	@Test
	fun `empty store EXPECT no queries`() {
		whenever(recentQueryRepository.read()) doReturn null

		assertEquals(emptyList<String>(), useCase.read())
	}

	@Test
	fun `stored lines EXPECT queries`() {
		whenever(recentQueryRepository.read()) doReturn "пуши\nВласов"

		assertEquals(listOf("пуши", "Власов"), useCase.read())
	}

	@Test
	fun `remember EXPECT trimmed query first`() {
		whenever(recentQueryRepository.read()) doReturn "пуши"

		useCase.remember("  Власов  ")

		verify(recentQueryRepository).save("Власов\nпуши")
	}

	@Test
	fun `same query EXPECT moved first`() {
		whenever(recentQueryRepository.read()) doReturn "пуши\nВласов"

		useCase.remember("Власов")

		verify(recentQueryRepository).save("Власов\nпуши")
	}

	@Test
	fun `blank EXPECT nothing saved`() {
		useCase.remember("   ")

		verify(recentQueryRepository, never()).save(any())
	}

	@Test
	fun `ninth query EXPECT oldest dropped`() {
		val stored = (1..8).joinToString("\n") { index -> "q$index" }
		whenever(recentQueryRepository.read()) doReturn stored

		useCase.remember("new")

		val kept = listOf("new") + (1..7).map { index -> "q$index" }
		verify(recentQueryRepository).save(kept.joinToString("\n"))
	}

	@Test
	fun `forget EXPECT query removed`() {
		whenever(recentQueryRepository.read()) doReturn "пуши\nВласов"

		useCase.forget("пуши")

		verify(recentQueryRepository).save("Власов")
	}
}
