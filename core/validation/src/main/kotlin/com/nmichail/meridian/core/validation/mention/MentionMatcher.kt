package com.nmichail.meridian.core.validation.mention

object MentionMatcher {

	private val queryPattern = Regex("(^|\\s)@([\\p{L}\\p{N}_]*)$")

	private val tokenPattern = Regex("(^|\\s)@[\\p{L}\\p{N}_]*$")

	fun query(draft: String): String? {
		val match = queryPattern.find(draft) ?: return null
		return match.groupValues[2]
	}

	fun apply(draft: String, token: String): String =
		draft.replace(tokenPattern) { match ->
			match.groupValues[1] + "@" + token + " "
		}

	fun person(name: String, username: String, query: String): Boolean {
		if (query.isEmpty()) {
			return true
		}
		return name.contains(query, ignoreCase = true) ||
			username.contains(query, ignoreCase = true)
	}

	fun everyone(query: String): Boolean =
		query.isEmpty() ||
			"all".startsWith(query, ignoreCase = true) ||
			"все".startsWith(query, ignoreCase = true)
}
