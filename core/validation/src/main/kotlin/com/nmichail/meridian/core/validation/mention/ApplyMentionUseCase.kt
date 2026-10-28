package com.nmichail.meridian.core.validation.mention

import javax.inject.Inject

class ApplyMentionUseCase @Inject constructor() {

	operator fun invoke(draft: String, token: String): String = MentionMatcher.apply(draft, token)
}