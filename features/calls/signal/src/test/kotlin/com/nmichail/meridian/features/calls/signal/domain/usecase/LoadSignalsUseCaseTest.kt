package com.nmichail.meridian.features.calls.signal.domain.usecase

import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadSignalsUseCaseTest {

	private val repository: SignalRepository = mock()
	private val useCase = LoadSignalsUseCase(repository)
	private val call = SignalCall(callId = "call-1", chatId = "chat-1")
	private val signals = listOf("offer", "ice")

	@Test
	fun `invoke EXPECT repository signals`() = runTest {
		whenever(repository.loadSignals(call)) doReturn signals

		val actual = useCase(call)

		assertEquals(signals, actual)
	}
}