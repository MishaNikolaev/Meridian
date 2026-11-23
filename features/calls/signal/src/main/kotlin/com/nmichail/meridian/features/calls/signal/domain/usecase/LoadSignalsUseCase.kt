package com.nmichail.meridian.features.calls.signal.domain.usecase

import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import javax.inject.Inject

class LoadSignalsUseCase @Inject constructor(
	private val repository: SignalRepository,
) : suspend (String, String) -> List<String> by repository::load
