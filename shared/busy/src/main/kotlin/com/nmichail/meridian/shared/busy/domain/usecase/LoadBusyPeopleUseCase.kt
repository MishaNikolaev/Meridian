package com.nmichail.meridian.shared.busy.domain.usecase

import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson
import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import javax.inject.Inject

class LoadBusyPeopleUseCase @Inject constructor(
	private val repository: BusyRepository,
) : suspend (String) -> List<BusyPerson> by repository::loadPeople