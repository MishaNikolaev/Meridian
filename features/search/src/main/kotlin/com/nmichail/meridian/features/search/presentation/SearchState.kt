package com.nmichail.meridian.features.search.presentation

enum class SearchSection {
	ALL,
	CHATS,
	PEOPLE,
	TEXT,
	FILES,
}

sealed interface SearchState {

	data object Initial : SearchState

	data class Content(
		val query: String,
		val section: SearchSection,
		val recent: List<String>,
	) : SearchState
}
