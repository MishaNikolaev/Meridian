package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.SessionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetAccessTokenUseCaseTest {

	private val sessionRepository: SessionRepository = mock()
	private val useCase = GetAccessTokenUseCase(sessionRepository)

	@Test
	fun `invoke EXPECT access token`() {
		whenever(sessionRepository.getAccessToken()) doReturn "token"

		val actual = useCase()

		assertEquals("token", actual)
	}
}
