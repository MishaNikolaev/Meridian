package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.JoinedChat
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
class AcceptChatInviteUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = AcceptChatInviteUseCase(repository)
	private val joined = JoinedChat(id = "chat", title = "Лаборатория")

	@Test
	fun `invoke EXPECT repository chat`() = runTest {
		whenever(repository.acceptInvite("code")) doReturn joined

		val actual = useCase("code")

		assertEquals(joined, actual)
	}
}