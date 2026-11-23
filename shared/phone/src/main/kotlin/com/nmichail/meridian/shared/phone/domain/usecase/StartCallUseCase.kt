package com.nmichail.meridian.shared.phone.domain.usecase

import com.nmichail.meridian.shared.phone.domain.entity.CallMedia
import com.nmichail.meridian.shared.phone.domain.entity.PlacedCall
import com.nmichail.meridian.shared.phone.domain.repository.PhoneRepository
import javax.inject.Inject

class StartCallUseCase @Inject constructor(
	private val repository: PhoneRepository,
) : suspend (String, CallMedia) -> PlacedCall by repository::startCall
