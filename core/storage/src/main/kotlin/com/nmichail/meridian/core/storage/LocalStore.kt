package com.nmichail.meridian.core.storage

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStore @Inject constructor(
	@ApplicationContext context: Context,
) {

	private companion object {

		const val PREFS_NAME = "meridian"
	}

	private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
	private val secrets = SecretCipher(context)

	fun getString(key: String): String? = sharedPreferences.getString(key, null)

	fun putString(key: String, value: String) {
		sharedPreferences.edit().putString(key, value).apply()
	}

	fun getSecret(key: String): String? {
		val stored = sharedPreferences.getString(key, null) ?: return null
		if (!secrets.isSealed(stored)) {
			putSecret(key, stored)
			return stored
		}
		return secrets.open(stored)
	}

	fun putSecret(key: String, value: String) {
		sharedPreferences.edit().putString(key, secrets.seal(value)).apply()
	}

	fun remove(key: String) {
		sharedPreferences.edit().remove(key).apply()
	}
}
