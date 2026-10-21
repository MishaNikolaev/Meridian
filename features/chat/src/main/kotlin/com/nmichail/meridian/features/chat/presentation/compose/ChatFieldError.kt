package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.compose.usernameErrorMessage
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameInvalidState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.features.chat.R

@Composable
internal fun titleError(title: DisplayNameValidationItem): String {
	val state = title.validationState
	if (state !is DefaultValidationState.Invalid) {
		return ""
	}
	val message = when (state.invalidState) {
		DisplayNameInvalidState.EMPTY -> R.string.chat_title_empty
		DisplayNameInvalidState.TOO_LONG -> R.string.chat_title_too_long
		else -> return ""
	}
	return stringResource(message)
}

@Composable
internal fun usernameError(username: UsernameValidationItem): String =
	stringResource(usernameErrorMessage(username.validationState))
