package com.nmichail.meridian.core.validation.displayName

import com.nmichail.meridian.core.validation.InvalidState

enum class DisplayNameInvalidState : InvalidState {
	EMPTY,
	TOO_LONG,
}
