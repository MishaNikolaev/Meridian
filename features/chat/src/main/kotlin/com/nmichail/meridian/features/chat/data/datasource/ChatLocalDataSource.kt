package com.nmichail.meridian.features.chat.data.datasource

import com.nmichail.meridian.core.storage.LocalPreferences
import javax.inject.Inject

interface ChatLocalDataSource {

	fun getText(): String

	fun save(text: String)
}

class ChatLocalDataSourceImpl @Inject constructor(
	private val localPreferences: LocalPreferences,
) : ChatLocalDataSource {

	private companion object {

		const val KEY = "home_text"
	}

	override fun getText(): String = localPreferences.getString(KEY).orEmpty()

	override fun save(text: String) {
		localPreferences.putString(KEY, text)
	}
}
