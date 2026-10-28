package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.storage.ApplicationContext
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface ReactionDataSource {

	fun getReactions(): Map<String, String>

	fun saveReaction(messageId: String, reaction: String)
}

class ReactionDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : ReactionDataSource {

	private companion object {

		const val PREFERENCES = "meridian"
		const val REACTIONS = "message_reactions"
	}

	private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

	override fun getReactions(): Map<String, String> {
		val text = preferences.getString(REACTIONS, null).orEmpty()
		if (text.isEmpty()) {
			return emptyMap()
		}
		return try {
			Json.decodeFromString(text)
		} catch (_: SerializationException) {
			emptyMap()
		}
	}

	override fun saveReaction(messageId: String, reaction: String) {
		val saved = getReactions() + (messageId to reaction)
		preferences.edit { putString(REACTIONS, Json.encodeToString(saved)) }
	}
}