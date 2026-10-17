package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.InvalidState

enum class UsernameInvalidState : InvalidState {
	INVALID_LENGTH,
	INVALID_CHARACTERS,
}
