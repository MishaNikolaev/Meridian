package com.nmichail.meridian.features.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.storage.ApplicationContext
import javax.inject.Inject

interface ChatLocalDataSource {

	fun getText(): String

	fun save(text: String)
}

class ChatLocalDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : ChatLocalDataSource {

	private companion object {

		const val CHAT_PREF = "meridian"
		const val KEY = "home_text"
	}

	private val preferences = context.getSharedPreferences(CHAT_PREF, Context.MODE_PRIVATE)

	override fun getText(): String = preferences.getString(KEY, null).orEmpty()

	override fun save(text: String) {
		preferences.edit { putString(KEY, text) }
	}
}