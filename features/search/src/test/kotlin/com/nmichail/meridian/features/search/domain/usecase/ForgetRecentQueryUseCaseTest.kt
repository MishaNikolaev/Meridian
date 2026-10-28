package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ForgetRecentQueryUseCaseTest {

	private val repository: RecentQueryRepository = mock()
	private val useCase = ForgetRecentQueryUseCase(repository)

	@Test
	fun `forget EXPECT query removed`() {
		whenever(repository.read()) doReturn "пуши\nВласов"

		useCase("пуши")

		verify(repository).save("Власов")
	}
}
