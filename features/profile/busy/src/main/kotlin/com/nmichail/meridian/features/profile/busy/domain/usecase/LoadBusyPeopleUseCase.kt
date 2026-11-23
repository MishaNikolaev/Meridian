package com.nmichail.meridian.features.profile.busy.domain.usecase

import com.nmichail.meridian.features.profile.busy.domain.entity.BusyPerson
import com.nmichail.meridian.features.profile.busy.domain.repository.BusyRepository
import javax.inject.Inject

class LoadBusyPeopleUseCase @Inject constructor(
	private val repository: BusyRepository,
) : suspend (String) -> List<BusyPerson> by repository::loadPeople