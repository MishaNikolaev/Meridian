package com.nmichail.meridian.shared.group.domain.usecase

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class TransferChatUseCaseTest {

	private val repository: GroupRepository = mock()
	private val useCase = TransferChatUseCase(repository)

	@Test
	fun `invoke EXPECT repository transfer`() = runTest {
		useCase("chat", "user")

		verify(repository).transferChat("chat", "user")
	}
}