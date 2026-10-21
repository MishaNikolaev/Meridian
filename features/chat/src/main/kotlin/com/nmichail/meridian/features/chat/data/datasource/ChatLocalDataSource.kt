package com.nmichail.meridian.features.chat.data.datasource

import com.nmichail.meridian.core.storage.LocalStore
import javax.inject.Inject

interface ChatLocalDataSource {

	fun getText(): String

	fun save(text: String)
}

class ChatLocalDataSourceImpl @Inject constructor(
	private val localStore: LocalStore,
) : ChatLocalDataSource {

	private companion object {

		const val KEY = "home_text"
	}

	override fun getText(): String = localStore.getString(KEY).orEmpty()

	override fun save(text: String) {
		localStore.putString(KEY, text)
	}
}
