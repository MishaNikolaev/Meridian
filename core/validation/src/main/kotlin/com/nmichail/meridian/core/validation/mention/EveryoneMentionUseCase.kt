package com.nmichail.meridian.core.validation.mention

import javax.inject.Inject

class EveryoneMentionUseCase @Inject constructor() : (String) -> Boolean by MentionMatcher::everyone
