package com.nmichail.meridian.features.person.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.features.person.data.model.PersonEntry
import javax.inject.Inject

interface PersonLocalDataSource {
	suspend fun readPerson(userId: String): PersonEntry?

	suspend fun savePerson(userId: String, entry: PersonEntry)
}

class PersonLocalDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : PersonLocalDataSource {
	private companion object {
		const val PERSON_STORE = "meridian_person"
		const val PERSON_KEY = "person_"
	}

	private val preferences = context.getSharedPreferences(PERSON_STORE, Context.MODE_PRIVATE)

	override suspend fun readPerson(userId: String): PersonEntry? {
		val name = preferences.getString(key(userId, "name"), null) ?: return null

		return PersonEntry(
			name = name,
			position = preferences.getString(key(userId, "position"), "").orEmpty(),
			department = preferences.getString(key(userId, "department"), "").orEmpty(),
			office = preferences.getString(key(userId, "office"), "").orEmpty(),
			server = preferences.getString(key(userId, "server"), "").orEmpty(),
			status = preferences.getString(key(userId, "status"), "").orEmpty(),
			manager = preferences.getString(key(userId, "manager"), "").orEmpty(),
			managerId = preferences.getString(key(userId, "managerId"), "").orEmpty(),
			email = preferences.getString(key(userId, "email"), "").orEmpty(),
			phone = preferences.getString(key(userId, "phone"), "").orEmpty(),
			photo = preferences.getString(key(userId, "photo"), "").orEmpty(),
		)
	}

	override suspend fun savePerson(userId: String, entry: PersonEntry) {
		preferences.edit {
			putString(key(userId, "name"), entry.name)

			putString(key(userId, "position"), entry.position)

			putString(key(userId, "department"), entry.department)

			putString(key(userId, "office"), entry.office)

			putString(key(userId, "server"), entry.server)

			putString(key(userId, "status"), entry.status)

			putString(key(userId, "manager"), entry.manager)

			putString(key(userId, "managerId"), entry.managerId)

			putString(key(userId, "email"), entry.email)

			putString(key(userId, "phone"), entry.phone)

			putString(key(userId, "photo"), entry.photo)
		}
	}

	private fun key(userId: String, field: String): String = PERSON_KEY + userId + "_" + field
}
