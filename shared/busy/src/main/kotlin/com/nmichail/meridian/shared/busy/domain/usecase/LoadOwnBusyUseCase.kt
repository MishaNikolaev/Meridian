package com.nmichail.meridian.shared.busy.domain.usecase

import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import javax.inject.Inject

class LoadOwnBusyUseCase @Inject constructor(
	private val repository: BusyRepository,
) : suspend (String) -> List<Int> by repository::loadOwn
