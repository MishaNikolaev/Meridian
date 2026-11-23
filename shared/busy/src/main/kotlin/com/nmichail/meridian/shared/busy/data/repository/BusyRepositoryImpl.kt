package com.nmichail.meridian.shared.busy.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.busy.data.api.BusyApi
import com.nmichail.meridian.shared.busy.data.model.BusyRequestDto
import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson
import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import javax.inject.Inject

class BusyRepositoryImpl @Inject constructor(
	private val api: BusyApi,
	private val sessionRepository: SessionRepository,
) : BusyRepository {

	override suspend fun loadPeople(day: String): List<BusyPerson> =
		api.loadPeople(day).people.map { person ->
			BusyPerson(name = person.name, slots = person.slots.toSet())
		}

	override suspend fun loadOwn(day: String): List<Int> {
		val self = sessionRepository.getUserId().orEmpty()
		return api.loadPeople(day).people.firstOrNull { person -> person.userId == self }?.slots.orEmpty()
	}

	override suspend fun saveSlots(day: String, slots: List<Int>): List<Int> =
		api.saveSlots(BusyRequestDto(day = day, slots = slots)).slots
}
