package com.nmichail.meridian.shared.chat.domain.usecase

import javax.inject.Inject

class NamedMentionUseCase @Inject constructor() : (String) -> Boolean {
	override fun invoke(text: String): Boolean =
		mentionTokens(text).any { token -> !token.equals("all", ignoreCase = true) }
}
