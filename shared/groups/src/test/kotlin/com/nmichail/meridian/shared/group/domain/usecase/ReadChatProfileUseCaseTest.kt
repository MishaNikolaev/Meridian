package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
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
class ReadChatProfileUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = ReadChatProfileUseCase(repository)
	private val profile = ChatProfile(
		title = "Лаборатория",
		description = "Описание",
		owner = "Волков Сергей",
		members = emptyList(),
		selfId = "self",
		photoId = "photo",
	)

	@Test
	fun `invoke EXPECT repository profile`() = runTest {
		whenever(repository.readChat("chat")) doReturn profile

		val actual = useCase("chat")

		assertEquals(profile, actual)
	}
}