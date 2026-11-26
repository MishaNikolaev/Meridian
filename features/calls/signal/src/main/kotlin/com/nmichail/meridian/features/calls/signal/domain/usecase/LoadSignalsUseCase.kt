package com.nmichail.meridian.features.calls.signal.domain.usecase

import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import javax.inject.Inject

class LoadSignalsUseCase @Inject constructor(
	private val repository: SignalRepository,
) : suspend (SignalCall) -> List<String> by repository::loadSignals