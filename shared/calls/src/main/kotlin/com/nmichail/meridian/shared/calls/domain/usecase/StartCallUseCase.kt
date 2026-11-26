package com.nmichail.meridian.shared.calls.domain.usecase

import com.nmichail.meridian.shared.calls.domain.entity.CallMedia
import com.nmichail.meridian.shared.calls.domain.entity.PlacedCall
import com.nmichail.meridian.shared.calls.domain.repository.PhoneRepository
import javax.inject.Inject

class StartCallUseCase @Inject constructor(
	private val repository: PhoneRepository,
) : suspend (String, CallMedia) -> PlacedCall by repository::startCall
