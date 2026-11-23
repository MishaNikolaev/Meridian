package com.nmichail.meridian.features.calls.signal.presentation

import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.shared.phone.domain.entity.CallPhase
import com.nmichail.meridian.features.calls.signal.domain.usecase.LoadSignalsUseCase
import com.nmichail.meridian.features.calls.signal.domain.usecase.SendSignalUseCase
import com.nmichail.meridian.shared.phone.domain.usecase.StartCallUseCase
import com.nmichail.meridian.shared.phone.domain.usecase.UpdateCallUseCase
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

private const val SIGNAL_GAP = 100L
private const val INBOUND_BUFFER = 64
private const val SIGNAL_SEND = 6

class CallSignalViewModel @Inject constructor(
	private val startCallUseCase: StartCallUseCase,
	private val updateCallUseCase: UpdateCallUseCase,
	private val sendSignalUseCase: SendSignalUseCase,
	private val loadSignalsUseCase: LoadSignalsUseCase,
	private val errorDelegate: ErrorDelegate,
) : BaseViewModel<CallSignalState, CallSignalIntent, CallSignalEvent>() {

	private val outgoing = Channel<String>(capacity = Channel.UNLIMITED)
	private val _inbound = MutableSharedFlow<String>(extraBufferCapacity = INBOUND_BUFFER)
	val inbound: SharedFlow<String> = _inbound.asSharedFlow()
	private var job: Job? = null
	private var pending: CallPhase? = null
	private var placeFailed = false
	private var pollError: String? = null

	override fun initState(): CallSignalState = CallSignalState.Idle

	override fun applyIntent(intent: CallSignalIntent) {
		when (intent) {
			is CallSignalIntent.Place -> handlePlace(intent)
			is CallSignalIntent.Update -> handleUpdate(intent)
			is CallSignalIntent.Start -> handleStart(intent)
			CallSignalIntent.Stop -> handleStop()
			is CallSignalIntent.Outgoing -> outgoing.trySend(intent.body)
		}
	}

	override fun onCleared() {
		handleStop()
		super.onCleared()
	}

	private fun handlePlace(intent: CallSignalIntent.Place) {
		launchTry {
			val placed = startCallUseCase(intent.chatId, intent.media)
			setEvent(CallSignalEvent.Placed(placed.callId))
			val waiting = pending
			pending = null
			if (waiting != null) {
				publish(placed.callId, waiting)
			}
		} catch { error ->
			placeFailed = true
			val waiting = pending
			pending = null
			errorDelegate.processNonBlockingError(error)
			if (waiting != null) {
				setEvent(CallSignalEvent.Updated(waiting))
			}
		}
	}

	private fun handleUpdate(intent: CallSignalIntent.Update) {
		if (intent.callId.isEmpty()) {
			if (placeFailed) {
				setEvent(CallSignalEvent.Updated(intent.phase))
			} else {
				pending = intent.phase
			}
			return
		}
		launchTry {
			publish(intent.callId, intent.phase)
		} catch { error ->
			errorDelegate.processNonBlockingError(error)
		}
	}

	private suspend fun publish(callId: String, phase: CallPhase) {
		updateCallUseCase(callId, phase)
		setEvent(CallSignalEvent.Updated(phase))
	}

	private fun handleStart(intent: CallSignalIntent.Start) {
		if (intent.callId.isEmpty() || intent.chatId.isEmpty()) {
			return
		}
		val next = CallSignalState.Live(
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
			coroutineScope {
				launch { send(callId = intent.callId, chatId = intent.chatId) }
				launch { read(callId = intent.callId, chatId = intent.chatId) }
			}
		} catch { error ->
			errorDelegate.processNonBlockingError(error)
		}
	}

	private fun handleStop() {
		job?.cancel()
		job = null
		pollError = null
		if (state.value is CallSignalState.Live) {
			setState(CallSignalState.Idle)
		}
	}

	private suspend fun send(callId: String, chatId: String) {
		val gate = Semaphore(permits = SIGNAL_SEND)
		coroutineScope {
			for (body in outgoing) {
				gate.acquire()
				launch {
					try {
						sendSignalUseCase(callId, chatId, body)
					} catch (error: CancellationException) {
						throw error
					} catch (error: Exception) {
						errorDelegate.processNonBlockingError(error)
					} finally {
						gate.release()
					}
				}
			}
		}
	}

	private suspend fun read(callId: String, chatId: String) {
		while (coroutineContext.isActive) {
			val signals = try {
				val loaded = loadSignalsUseCase(callId, chatId)
				pollError = null
				loaded
			} catch (error: CancellationException) {
				throw error
			} catch (error: Exception) {
				reportPoll(error)
				emptyList()
			}
			signals.forEach { body -> _inbound.tryEmit(body) }
			delay(timeMillis = SIGNAL_GAP)
		}
	}

	private fun reportPoll(error: Exception) {
		val key = error::class.java.name + error.message
		if (pollError == key) {
			return
		}
		pollError = key
		errorDelegate.processNonBlockingError(error)
	}
}
