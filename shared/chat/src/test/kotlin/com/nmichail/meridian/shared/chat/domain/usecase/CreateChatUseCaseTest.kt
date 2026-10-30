package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class CreateChatUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = CreateChatUseCase(repository)
	private val created = CreatedChat(
		id = "1",
		title = "Обсуждение",
		corporate = true,
	)

	@Test
	fun `invoke EXPECT created chat`() = runTest {
		whenever(repository.create(ChatType.GROUP, "Обсуждение", listOf("member"))) doReturn created

		val actual = useCase(ChatType.GROUP, "Обсуждение", listOf("member"))

		assertEquals(created, actual)
	}
}