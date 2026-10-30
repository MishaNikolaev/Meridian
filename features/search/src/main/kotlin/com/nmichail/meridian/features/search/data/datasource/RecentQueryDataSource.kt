package com.nmichail.meridian.features.search.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import javax.inject.Inject

class RecentQueryDataSource @Inject constructor(
	@ApplicationContext context: Context,
) : RecentQueryRepository {

	private companion object {

		const val SEARCH_PREF = "meridian"
		const val KEY = "search_recent"
	}

	private val preferences = context.getSharedPreferences(SEARCH_PREF, Context.MODE_PRIVATE)

	override fun read(): String? = preferences.getString(KEY, null)

	override fun save(value: String) {
		preferences.edit { putString(KEY, value) }
	}
}
