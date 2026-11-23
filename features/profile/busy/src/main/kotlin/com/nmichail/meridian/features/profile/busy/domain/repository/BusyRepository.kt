package com.nmichail.meridian.features.profile.busy.domain.repository

import com.nmichail.meridian.features.profile.busy.domain.entity.BusyPerson

interface BusyRepository {

	suspend fun loadPeople(day: String): List<BusyPerson>

	suspend fun loadOwn(day: String): List<Int>

	suspend fun saveSlots(day: String, slots: List<Int>): List<Int>
}