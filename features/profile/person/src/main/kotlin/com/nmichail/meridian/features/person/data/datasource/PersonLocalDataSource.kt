package com.nmichail.meridian.features.person.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.features.person.data.dto.PersonDto
import com.nmichail.meridian.features.person.data.mapper.readPeople
import com.nmichail.meridian.features.person.data.mapper.writePeople
import javax.inject.Inject
import kotlinx.serialization.json.Json

interface PersonLocalDataSource {
	suspend fun readPerson(userId: String): PersonDto?

	suspend fun savePerson(userId: String, person: PersonDto)
}

class PersonLocalDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
	private val json: Json,
) : PersonLocalDataSource {
	private companion object {
		const val PERSON_STORE = "meridian_person"
		const val PERSON_KEY = "PERSON_KEY"
	}

	private val preferences = context.getSharedPreferences(PERSON_STORE, Context.MODE_PRIVATE)

	override suspend fun readPerson(userId: String): PersonDto? {
		val saved = preferences.getString(PERSON_KEY, null) ?: return null

		return json.readPeople(saved)[userId]
	}

	override suspend fun savePerson(userId: String, person: PersonDto) {
		val saved = preferences.getString(PERSON_KEY, null)
		val stored = if (saved == null) {
			emptyMap()
		} else {
			json.readPeople(saved)
		}

		preferences.edit {
			putString(PERSON_KEY, json.writePeople(stored + (userId to person)))
		}
	}
}