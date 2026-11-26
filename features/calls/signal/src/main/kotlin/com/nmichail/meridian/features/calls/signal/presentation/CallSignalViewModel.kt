package com.nmichail.meridian.features.calls.signal.presentation

import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.usecase.LoadSignalsUseCase
import com.nmichail.meridian.features.calls.signal.domain.usecase.SendSignalUseCase
import com.nmichail.meridian.shared.calls.domain.usecase.StartCallUseCase
import com.nmichail.meridian.shared.calls.domain.usecase.UpdateCallUseCase
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class CallSignalViewModel @Inject constructor(
	private val startCallUseCase: StartCallUseCase,
	private val updateCallUseCase: UpdateCallUseCase,
	private val sendSignalUseCase: SendSignalUseCase,
	private val loadSignalsUseCase: LoadSignalsUseCase,
	private val errorDelegate: ErrorDelegate,
) : BaseViewModel<CallSignalState, CallSignalIntent, CallSignalEvent>() {

	private companion object {
		const val SIGNAL_GAP = 100L
	}

	private var job: Job? = null
	private var update: CallSignalIntent.Update? = null

	override fun initState(): CallSignalState = CallSignalState.Initial

	override fun applyIntent(intent: CallSignalIntent) {
		when (intent) {
			is CallSignalIntent.Place -> handlePlace(intent)
			is CallSignalIntent.Update -> handleUpdate(intent)
			is CallSignalIntent.Start -> handleStart(intent)
			is CallSignalIntent.Stop -> handleStop()
			is CallSignalIntent.Outgoing -> handleOutgoing(intent)
		}
	}

	private fun handlePlace(intent: CallSignalIntent.Place) {
		if (state.value is CallSignalState.Loading) {
			return
		}
		setState(CallSignalState.Loading)
		launchTry {
			handleStartCall(intent)
		} catch ::handlePlaceFailure
	}

	private suspend fun handleStartCall(intent: CallSignalIntent.Place) {
		val call = startCallUseCase(intent.chatId, intent.media)
		setEvent(CallSignalEvent.Placed(call.callId))
		val current = update
		update = null
		if (current != null) {
			handleUpdateCall(current.copy(callId = call.callId))
		}
	}

	private fun handlePlaceFailure(error: Exception) {
		val current = update
		update = null
		setState(CallSignalState.Error(message = error.userMessage()))
		errorDelegate.processNonBlockingError(error)
		if (current != null) {
			setEvent(CallSignalEvent.Updated(current.phase))
		}
	}

	private fun handleUpdate(intent: CallSignalIntent.Update) {
		if (intent.callId.isEmpty()) {
			if (state.value is CallSignalState.Error) {
				setEvent(CallSignalEvent.Updated(intent.phase))
			} else {
				update = intent
			}
			return
		}

		launchTry {
			handleUpdateCall(intent)
		} catch { error ->
			errorDelegate.processNonBlockingError(error)
		}
	}

	private suspend fun handleUpdateCall(intent: CallSignalIntent.Update) {
		updateCallUseCase(intent.callId, intent.phase)
		setEvent(CallSignalEvent.Updated(intent.phase))
	}

	private fun handleStart(intent: CallSignalIntent.Start) {
		if (intent.callId.isEmpty() || intent.chatId.isEmpty()) {
			return
		}
		val next = CallSignalState.Content(
			callId = intent.callId,
			chatId = intent.chatId,
			incoming = intent.incoming,
		)
		if (state.value == next) {
			return
		}

		handleStop()
		setState(next)

		job = launchTry {
			handleRead(intent)
		} catch ::handleReadFailure
	}

	private fun handleReadFailure(error: Exception) {
		if (state.value is CallSignalState.Content) {
			errorDelegate.processNonBlockingError(error)
			return
		}
		setState(CallSignalState.Error(message = error.userMessage()))
	}

	private fun handleStop() {
		job?.cancel()
		job = null
		if (state.value is CallSignalState.Content) {
			setState(CallSignalState.Initial)
		}
	}

	private fun handleOutgoing(intent: CallSignalIntent.Outgoing) {
		val content = state.value as? CallSignalState.Content ?: return
		launchTry {
			sendSignalUseCase(SignalCall(content.callId, content.chatId), intent.body)
		} catch { error ->
			errorDelegate.processNonBlockingError(error)
		}
	}

	private suspend fun handleRead(intent: CallSignalIntent.Start) {
		while (coroutineContext.isActive) {
			val signals = loadSignalsUseCase(SignalCall(intent.callId, intent.chatId))
			signals.forEach { body -> setEvent(CallSignalEvent.Signal(body)) }
			delay(timeMillis = SIGNAL_GAP)
		}
	}
}