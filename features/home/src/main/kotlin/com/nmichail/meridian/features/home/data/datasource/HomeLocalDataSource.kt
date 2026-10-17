package com.nmichail.meridian.features.home.data.datasource

import com.nmichail.meridian.core.storage.LocalStore
import javax.inject.Inject

interface HomeLocalDataSource {

	fun text(): String

	fun save(text: String)
}

class HomeLocalDataSourceImpl @Inject constructor(
	private val localStore: LocalStore,
) : HomeLocalDataSource {

	private companion object {

		const val KEY = "home_text"
	}

	override fun text(): String = localStore.getString(KEY).orEmpty()

	override fun save(text: String) {
		localStore.putString(KEY, text)
	}
}
