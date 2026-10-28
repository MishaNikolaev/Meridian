package com.nmichail.meridian.features.search.presentation

sealed interface SearchIntent {

	data object Load : SearchIntent

	data class ChangeQuery(
		val query: String,
	) : SearchIntent

	data class ChangeSection(
		val section: SearchSection,
	) : SearchIntent

	data object Submit : SearchIntent

	data class OpenRecent(
		val query: String,
	) : SearchIntent

	data class RemoveRecent(
		val query: String,
	) : SearchIntent

	data object NavigateBack : SearchIntent
}
