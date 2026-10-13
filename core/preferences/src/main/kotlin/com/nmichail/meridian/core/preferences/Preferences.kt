package com.nmichail.meridian.core.preferences

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(
	@ApplicationContext context: Context,
) {

	private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

	fun getString(key: String): String? = sharedPreferences.getString(key, null)

	fun putString(key: String, value: String) {
		sharedPreferences.edit().putString(key, value).apply()
	}

	private companion object {

		const val PREFS_NAME = "meridian"
	}
}
