package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import javax.inject.Inject

interface WorkspaceLocalDataSource {

	fun loadHost(): String?

	fun saveHost(host: String)

	fun loadEmail(): String?

	fun saveEmail(value: String): String?

	fun clearWorkspace()
}

class WorkspaceLocalDataSourceImpl @Inject constructor(
	@ApplicationContext context: Context,
) : WorkspaceLocalDataSource {

	private companion object {

		const val SESSION_PREF = "meridian"
		const val HOST = "session_host"
		const val EMAIL = "session_email"
	}

	private val preferences = context.getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)

	override fun loadHost(): String? =
		preferences.getString(HOST, null)?.takeIf { host -> host.isNotBlank() }

	override fun saveHost(host: String) {
		val trimmed = host.trim()
		if (trimmed.isEmpty()) {
			preferences.edit { remove(HOST) }
			return
		}
		preferences.edit { putString(HOST, trimmed) }
	}

	override fun loadEmail(): String? =
		preferences.getString(EMAIL, null)?.takeIf { email -> email.isNotBlank() }

	override fun saveEmail(value: String): String? {
		val trimmed = value.trim()
		if (trimmed.isEmpty()) {
			preferences.edit { remove(EMAIL) }
			return null
		}
		preferences.edit { putString(EMAIL, trimmed) }
		return trimmed
	}

	override fun clearWorkspace() {
		preferences.edit {
			remove(HOST)
			remove(EMAIL)
		}
	}
}
