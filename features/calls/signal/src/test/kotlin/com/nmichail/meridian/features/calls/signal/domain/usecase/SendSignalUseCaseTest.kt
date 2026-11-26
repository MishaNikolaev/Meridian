package com.nmichail.meridian.features.calls.signal.domain.usecase

import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class SendSignalUseCaseTest {

	private val repository: SignalRepository = mock()
	private val useCase = SendSignalUseCase(repository)
	private val call = SignalCall(callId = "call-1", chatId = "chat-1")
	private val body = "offer"

	@Test
	fun `invoke EXPECT repository signal`() = runTest {
		useCase(call, body)

		verify(repository).sendSignal(call, body)
	}
}