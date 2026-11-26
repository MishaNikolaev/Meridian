package com.nmichail.meridian.shared.chat.domain.usecase

import javax.inject.Inject

class MentionedUseCase @Inject constructor() : (String, String) -> Boolean {
	override fun invoke(text: String, username: String): Boolean =
		mentionTokens(text).any { token ->
			token.equals("all", ignoreCase = true) ||
				(username.isNotEmpty() && token.equals(username, ignoreCase = true))
		}
}
