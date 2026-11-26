package com.nmichail.meridian.shared.chat.domain.usecase

private val tokens = Regex("@([\\p{L}\\p{N}_]+)")

internal fun mentionTokens(text: String): List<String> =
	tokens.findAll(text).map { match -> match.groupValues[1] }.toList()
