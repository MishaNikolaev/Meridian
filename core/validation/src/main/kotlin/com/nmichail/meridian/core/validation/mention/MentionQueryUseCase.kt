package com.nmichail.meridian.core.validation.mention

import javax.inject.Inject

class MentionQueryUseCase @Inject constructor() : (String) -> String? {

	override fun invoke(draft: String): String? = MentionMatcher.query(draft)
}
