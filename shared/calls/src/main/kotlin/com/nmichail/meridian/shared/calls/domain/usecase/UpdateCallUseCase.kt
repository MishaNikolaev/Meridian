package com.nmichail.meridian.shared.calls.domain.usecase

import com.nmichail.meridian.shared.calls.domain.entity.CallPhase
import com.nmichail.meridian.shared.calls.domain.entity.PlacedCall
import com.nmichail.meridian.shared.calls.domain.repository.PhoneRepository
import javax.inject.Inject

class UpdateCallUseCase @Inject constructor(
	private val repository: PhoneRepository,
) : suspend (String, CallPhase) -> PlacedCall by repository::updateCall