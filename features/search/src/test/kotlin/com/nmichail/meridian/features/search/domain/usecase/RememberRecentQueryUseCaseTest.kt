package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RememberRecentQueryUseCaseTest {

	private val repository: RecentQueryRepository = mock()
	private val useCase = RememberRecentQueryUseCase(repository)

	@Test
	fun `remember EXPECT trimmed query first`() {
		whenever(repository.read()) doReturn "пуши"

		useCase("  Власов  ")

		verify(repository).save("Власов\nпуши")
	}

	@Test
	fun `same query EXPECT moved first`() {
		whenever(repository.read()) doReturn "пуши\nВласов"

		useCase("Власов")

		verify(repository).save("Власов\nпуши")
	}

	@Test
	fun `blank EXPECT nothing saved`() {
		useCase("   ")

		verify(repository, never()).save(any())
	}

	@Test
	fun `ninth query EXPECT oldest dropped`() {
		val stored = (1..8).joinToString("\n") { index -> "q$index" }
		whenever(repository.read()) doReturn stored

		useCase("new")

		val kept = listOf("new") + (1..7).map { index -> "q$index" }
		verify(repository).save(kept.joinToString("\n"))
	}
}
