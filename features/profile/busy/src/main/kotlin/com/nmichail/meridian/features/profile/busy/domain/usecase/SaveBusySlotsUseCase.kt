package com.nmichail.meridian.features.profile.busy.domain.usecase

import com.nmichail.meridian.features.profile.busy.domain.repository.BusyRepository
import javax.inject.Inject

class SaveBusySlotsUseCase @Inject constructor(
	private val repository: BusyRepository,
) : suspend (String, List<Int>) -> List<Int> by repository::saveSlots