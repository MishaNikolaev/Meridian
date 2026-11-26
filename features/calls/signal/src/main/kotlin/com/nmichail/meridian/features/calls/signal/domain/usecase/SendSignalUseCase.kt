package com.nmichail.meridian.features.calls.signal.domain.usecase

import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import javax.inject.Inject

class SendSignalUseCase @Inject constructor(
	private val repository: SignalRepository,
) : suspend (SignalCall, String) -> Unit by repository::sendSignal