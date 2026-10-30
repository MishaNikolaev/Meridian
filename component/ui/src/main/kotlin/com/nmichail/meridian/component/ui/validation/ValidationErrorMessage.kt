package com.nmichail.meridian.component.ui.validation

import androidx.compose.runtime.Composable
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameInvalidState
import com.nmichail.meridian.core.validation.password.PasswordInvalidState
import com.nmichail.meridian.core.validation.username.UsernameInvalidState

@Composable
fun usernameErrorMessage(state: DefaultValidationState): Int {
	if (state !is DefaultValidationState.Invalid) {
		return R.string.empty
	}

	return when (state.invalidState) {
		UsernameInvalidState.INVALID_LENGTH -> R.string.validation_error_username_length
		UsernameInvalidState.INVALID_CHARACTERS -> R.string.validation_error_username_characters
		else -> R.string.empty
	}
}

@Composable
fun passwordErrorMessage(state: DefaultValidationState): Int {
	if (state !is DefaultValidationState.Invalid) {
		return R.string.empty
	}

	return when (state.invalidState) {
		PasswordInvalidState.INVALID_LENGTH -> R.string.validation_error_password_length
		else -> R.string.empty
	}
}

@Composable
fun displayNameErrorMessage(state: DefaultValidationState): Int {
	if (state !is DefaultValidationState.Invalid) {
		return R.string.empty
	}

	return when (state.invalidState) {
		DisplayNameInvalidState.EMPTY -> R.string.validation_error_display_name_empty
		DisplayNameInvalidState.TOO_LONG -> R.string.validation_error_display_name_too_long
		else -> R.string.empty
	}
}
