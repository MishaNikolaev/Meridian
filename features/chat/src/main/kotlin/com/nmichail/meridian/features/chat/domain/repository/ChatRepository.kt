package com.nmichail.meridian.features.chat.domain.repository

interface ChatRepository {

	fun getText(): String

	fun save(text: String)
}
