package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class CreateChatInviteUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = CreateChatInviteUseCase(repository)

	@Test
	fun `invoke EXPECT repository code`() = runTest {
		whenever(repository.createInvite("chat")) doReturn "code"

		val actual = useCase("chat")

		assertEquals("code", actual)
	}
}