package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import javax.inject.Inject
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

interface ReactionDataSource {

	fun getReactions(): Map<String, Set<String>>

	fun saveReactions(messageId: String, reactions: Set<String>)

	fun saveReactions(updates: Map<String, Set<String>>)

	fun used(): List<String>

	fun saveUsed(reaction: String)
}

class ReactionDataSourceImpl internal constructor(
	private val preferences: SharedPreferences,
) : ReactionDataSource {

	@Inject
	constructor(
		@ApplicationContext context: Context,
	) : this(context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE))

	private companion object {
		const val PREFERENCES = "meridian"
		const val REACTIONS = "message_reactions"
		const val USED = "reaction_recent"
		const val USED_LIMIT = 32
	}

	override fun getReactions(): Map<String, Set<String>> {
		val text = preferences.getString(REACTIONS, null).orEmpty()

		if (text.isEmpty()) {
			return emptyMap()
		}

		val element = try {
			Json.parseToJsonElement(text)
		} catch (_: SerializationException) {
			return emptyMap()
		}

		val obj = element as? JsonObject ?: return emptyMap()

		return obj.entries.associate { (key, value) ->
			val reactions = when (value) {
				is JsonArray -> value.mapNotNull { item ->
					(item as? JsonPrimitive)?.contentOrNull
				}.toSet()

				is JsonPrimitive -> setOf(value.content)
				else -> emptySet()
			}

			key to reactions
		}
	}

	override fun saveReactions(messageId: String, reactions: Set<String>) {
		saveReactions(mapOf(messageId to reactions))
	}

	override fun saveReactions(updates: Map<String, Set<String>>) {
		if (updates.isEmpty()) {
			return
		}

		val saved = getReactions() + updates

		val encoded = saved.mapValues { (_, glyphs) -> glyphs.toList() }

		preferences.edit { putString(REACTIONS, Json.encodeToString(encoded)) }
	}

	override fun used(): List<String> {
		val text = preferences.getString(USED, null).orEmpty()

		if (text.isEmpty()) {
			return emptyList()
		}

		return try {
			Json.decodeFromString(text)
		} catch (_: SerializationException) {
			emptyList()
		}
	}

	override fun saveUsed(reaction: String) {
		val used = (used() + reaction).takeLast(USED_LIMIT)

		preferences.edit { putString(USED, Json.encodeToString(used)) }
	}
}