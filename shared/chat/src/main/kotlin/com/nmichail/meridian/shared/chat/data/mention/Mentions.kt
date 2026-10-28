package com.nmichail.meridian.shared.chat.data.mention

private val tokens = Regex("@([\\p{L}\\p{N}_]+)")

internal fun mentionTokens(text: String): List<String> =
	tokens.findAll(text).map { match -> match.groupValues[1] }.toList()

internal fun namedMention(text: String): Boolean =
	mentionTokens(text).any { token -> !token.equals("all", ignoreCase = true) }

internal fun mentioned(text: String, username: String): Boolean =
	mentionTokens(text).any { token ->
		token.equals("all", ignoreCase = true) ||
			(username.isNotEmpty() && token.equals(username, ignoreCase = true))
	}