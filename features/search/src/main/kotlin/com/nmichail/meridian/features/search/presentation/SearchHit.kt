package com.nmichail.meridian.features.search.presentation

//TODO Выпилить после фичи поиска
internal sealed interface SearchHit {

	val title: String

	data class Person(
		override val title: String,
		val note: String,
	) : SearchHit

	data class Chat(
		override val title: String,
		val note: String,
	) : SearchHit

	data class Message(
		override val title: String,
		val time: String,
		val preview: String,
	) : SearchHit

	data class File(
		override val title: String,
		val note: String,
	) : SearchHit
}
