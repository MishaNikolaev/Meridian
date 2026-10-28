package com.nmichail.meridian.core.validation.mention

import javax.inject.Inject

class MatchMentionUseCase @Inject constructor() {

	operator fun invoke(name: String, username: String, query: String): Boolean =
		MentionMatcher.person(name, username, query)
}
