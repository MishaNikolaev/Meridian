package com.nmichail.meridian.component.photopicker

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

private const val PREFERENCES = "meridian"
private const val PERSON_PHOTOS = "person_photos"
private const val CHAT_PHOTOS = "chat_photos"

object PhotoLocalDataSource {
	internal var ids by mutableStateOf(emptyMap<String, String>())
	internal var chats by mutableStateOf(emptyMap<String, String>())
	private var preferences: SharedPreferences? = null

	fun notePhoto(userId: String, photoId: String) {
		if (userId.isEmpty() || photoId.isEmpty() || ids[userId] == photoId) {
			return
		}

		ids = ids + (userId to photoId)
		write(PERSON_PHOTOS, ids)
	}

	fun load(context: Context) {
		if (preferences != null) {
			return
		}

		val stored = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

		preferences = stored

		ids = read(stored.getString(PERSON_PHOTOS, "").orEmpty())
		chats = read(stored.getString(CHAT_PHOTOS, "").orEmpty())
	}

	fun storedChatPhoto(chatId: String): String = chats[chatId].orEmpty()

	fun keepChatPhoto(chatId: String, photoId: String) {
		if (chatId.isEmpty() || photoId.isEmpty() || chats[chatId] == photoId) {
			return
		}

		chats = chats + (chatId to photoId)
		write(CHAT_PHOTOS, chats)
	}

	private fun read(raw: String): Map<String, String> {
		if (raw.isEmpty()) {
			return emptyMap()
		}

		return raw.lineSequence().mapNotNull { line ->
			val parts = line.split(" ", limit = 2)

			if (parts.size < 2 || parts[1].isEmpty()) {
				null
			} else {
				parts[0] to parts[1]
			}
		}.toMap()
	}

	private fun write(key: String, values: Map<String, String>) {
		val saved = values.entries.joinToString(separator = "\n") { (id, photo) -> "$id $photo" }

		preferences?.edit()?.putString(key, saved)?.apply()
	}
}