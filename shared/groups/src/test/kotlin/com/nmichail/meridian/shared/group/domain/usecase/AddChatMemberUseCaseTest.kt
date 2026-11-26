package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class AddChatMemberUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = AddChatMemberUseCase(repository)

	@Test
	fun `invoke EXPECT repository add`() = runTest {
		useCase("chat", "user")

		verify(repository).addChat("chat", "user")
	}
}