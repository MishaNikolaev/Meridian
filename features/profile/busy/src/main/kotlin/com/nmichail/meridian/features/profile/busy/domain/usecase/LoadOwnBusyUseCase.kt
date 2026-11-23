package com.nmichail.meridian.features.profile.busy.domain.usecase

import com.nmichail.meridian.features.profile.busy.domain.repository.BusyRepository
import javax.inject.Inject

class LoadOwnBusyUseCase @Inject constructor(
	private val repository: BusyRepository,
) : suspend (String) -> List<Int> by repository::loadOwn