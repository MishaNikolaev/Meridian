package com.nmichail.meridian.features.calls.signal.presentation

import androidx.lifecycle.Observer
import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.usecase.LoadSignalsUseCase
import com.nmichail.meridian.features.calls.signal.domain.usecase.SendSignalUseCase
import com.nmichail.meridian.shared.phone.domain.entity.CallMedia
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase
import com.nmichail.meridian.shared.phone.domain.entity.PlacedCall
import com.nmichail.meridian.shared.phone.domain.usecase.StartCallUseCase
import com.nmichail.meridian.shared.phone.domain.usecase.UpdateCallUseCase
import com.nmichail.meridian.testutils.InstantExecutorExtension
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class, InstantExecutorExtension::class)
class CallSignalViewModelTest {

	private val startCallUseCase: StartCallUseCase = mock()
	private val updateCallUseCase: UpdateCallUseCase = mock()
	private val sendSignalUseCase: SendSignalUseCase = mock()
	private val loadSignalsUseCase: LoadSignalsUseCase = mock()
	private val errorDelegate: ErrorDelegate = mock()
	private val eventObserver: Observer<CallSignalEvent> = mock()
	private val viewModel = CallSignalViewModel(
		startCallUseCase,
		updateCallUseCase,
		sendSignalUseCase,
		loadSignalsUseCase,
		errorDelegate,
	)
	private val call = PlacedCall(
		callId = "call-1",
		chatId = "chat-1",
		initiatorUserId = "self",
		state = CallPhase.RINGING,
	)
	private val ioException = IOException()

	@Test
	fun `init EXPECT initial`() {
		assertEquals(CallSignalState.Initial, viewModel.state.value)
	}

	@Test
	fun `place EXPECT placed call`() = runTest {
		whenever(startCallUseCase.invoke("chat-1", CallMedia.AUDIO)) doReturn call
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.AUDIO))

		verify(eventObserver).onChanged(CallSignalEvent.Placed(call.callId))
	}

	@Test
	fun `place while loading EXPECT one start`() = runTest {
		whenever(startCallUseCase.invoke(any(), any())) doReturn call

		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.AUDIO))
		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.VIDEO))

		verify(
			startCallUseCase,
			times(1),
		).invoke(
			any(),
			any(),
		)
	}

	@Test
	fun `update before place EXPECT published phase`() = runTest {
		whenever(startCallUseCase.invoke("chat-1", CallMedia.AUDIO)) doReturn call
		viewModel.event.observeForever(eventObserver)
		viewModel.applyIntent(CallSignalIntent.Update(callId = "", phase = CallPhase.ENDED))

		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.AUDIO))

		verify(eventObserver).onChanged(CallSignalEvent.Updated(CallPhase.ENDED))
	}

	@Test
	fun `place failure EXPECT error`() = runTest {
		whenever(startCallUseCase.invoke("chat-1", CallMedia.AUDIO)).doThrowSafe(ioException)

		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.AUDIO))

		assertEquals(CallSignalState.Error(message = R.string.error_no_connection), viewModel.state.value)
	}

	@Test
	fun `update after failed place EXPECT local phase`() = runTest {
		whenever(startCallUseCase.invoke("chat-1", CallMedia.AUDIO)).doThrowSafe(ioException)
		viewModel.applyIntent(CallSignalIntent.Place(chatId = "chat-1", media = CallMedia.AUDIO))
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(CallSignalIntent.Update(callId = "", phase = CallPhase.MISSED))

		verify(eventObserver).onChanged(CallSignalEvent.Updated(CallPhase.MISSED))
	}

	@Test
	fun `update EXPECT published phase`() = runTest {
		whenever(updateCallUseCase.invoke("call-1", CallPhase.ACCEPTED)) doReturn call
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(CallSignalIntent.Update(callId = "call-1", phase = CallPhase.ACCEPTED))

		verify(eventObserver).onChanged(CallSignalEvent.Updated(CallPhase.ACCEPTED))
	}

	@Test
	fun `empty start EXPECT initial`() = runTest {
		viewModel.applyIntent(CallSignalIntent.Start(callId = "", chatId = "chat-1", incoming = false))

		assertEquals(CallSignalState.Initial, viewModel.state.value)
	}

	@Test
	fun `start EXPECT signal`() = runTest {
		whenever(loadSignalsUseCase.invoke(SignalCall("call-1", "chat-1"))) doReturn listOf("offer")
		viewModel.event.observeForever(eventObserver)

		viewModel.applyIntent(
			CallSignalIntent.Start(callId = "call-1", chatId = "chat-1", incoming = false),
		)
		viewModel.applyIntent(CallSignalIntent.Stop)

		verify(eventObserver).onChanged(CallSignalEvent.Signal("offer"))
	}

	@Test
	fun `outgoing before start EXPECT nothing sent`() = runTest {
		viewModel.applyIntent(CallSignalIntent.Outgoing(body = "offer"))

		verify(sendSignalUseCase, never()).invoke(any(), any())
	}

	@Test
	fun `outgoing EXPECT sent signal`() = runTest {
		whenever(loadSignalsUseCase.invoke(SignalCall("call-1", "chat-1"))) doReturn emptyList()

		viewModel.applyIntent(
			CallSignalIntent.Start(callId = "call-1", chatId = "chat-1", incoming = true),
		)
		viewModel.applyIntent(CallSignalIntent.Outgoing(body = "offer"))
		viewModel.applyIntent(CallSignalIntent.Stop)

		verify(sendSignalUseCase).invoke(SignalCall("call-1", "chat-1"), "offer")
	}
}