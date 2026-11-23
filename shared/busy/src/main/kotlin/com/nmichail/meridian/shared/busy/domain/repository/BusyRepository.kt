package com.nmichail.meridian.shared.busy.domain.repository

import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson

interface BusyRepository {

	suspend fun loadPeople(day: String): List<BusyPerson>

	suspend fun loadOwn(day: String): List<Int>

	suspend fun saveSlots(day: String, slots: List<Int>): List<Int>
}
