package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import javax.inject.Inject

interface DeletionDataSource {
	fun getDeleted(): Set<String>

	fun saveDeleted(messageId: String) {
		saveDeleted(setOf(messageId))
	}

	fun saveDeleted(messageIds: Collection<String>)
}

class DeletionDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : DeletionDataSource {
	private companion object {
		const val PREFERENCES = "meridian"
		const val DELETED_MESSAGES = "deleted_messages"
	}

	private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

	override fun getDeleted(): Set<String> {
		val deleted = try {
			preferences.getStringSet(DELETED_MESSAGES, null)
		} catch (_: ClassCastException) {
			null
		}

		if (deleted != null) {
			return deleted.toSet()
		}

		val text = preferences.getString(DELETED_MESSAGES, null)
			.orEmpty()
			.split(",")
			.filter { messageId -> messageId.isNotEmpty() }
			.toSet()

		if (text.isNotEmpty()) {
			preferences.edit { putStringSet(DELETED_MESSAGES, text) }
		}

		return text
	}

	override fun saveDeleted(messageIds: Collection<String>) {
		if (messageIds.isEmpty()) {
			return
		}

		val saved = getDeleted() + messageIds

		preferences.edit { putStringSet(DELETED_MESSAGES, saved) }
	}
}