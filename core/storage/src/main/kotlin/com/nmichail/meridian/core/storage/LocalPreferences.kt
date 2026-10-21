package com.nmichail.meridian.core.storage

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalPreferences @Inject constructor(
	@ApplicationContext context: Context,
) {

	private companion object {

		const val PREFS_NAME = "meridian"
	}

	private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
	private val secrets = SecretCipher(context)

	fun getString(key: String): String? = sharedPreferences.getString(key, null)

	fun putString(key: String, value: String) {
		sharedPreferences.edit {
			putString(key, value)
		}
	}

	fun getSecret(key: String): String? {
		val stored = sharedPreferences.getString(key, null) ?: return null
		if (!secrets.isSealed(stored)) {
			putSecret(key, stored)
			return stored
		}
		val opened = secrets.open(stored) ?: return null
		if (!secrets.isCurrent(stored)) {
			putSecret(key, opened)
		}
		return opened
	}

	fun putSecret(key: String, value: String) {
		sharedPreferences.edit {
			putString(key, secrets.seal(value))
		}
	}

	fun remove(key: String) {
		sharedPreferences.edit {
			remove(key)
		}
	}
}
