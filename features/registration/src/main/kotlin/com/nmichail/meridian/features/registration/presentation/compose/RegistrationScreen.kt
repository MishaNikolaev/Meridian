package com.nmichail.meridian.features.registration.presentation.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.theme.palette
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.validation.displayNameErrorMessage
import com.nmichail.meridian.component.ui.validation.passwordErrorMessage
import com.nmichail.meridian.component.ui.validation.usernameErrorMessage
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyDefaultText
import com.nmichail.meridian.component.ui.text.MUiBodyRegularText
import com.nmichail.meridian.component.ui.text.MUiCaptionSmallText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.features.registration.R
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.features.registration.presentation.RegistrationIntent
import com.nmichail.meridian.features.registration.presentation.RegistrationState

@Composable
fun RegistrationScreen(
	state: StateFlow<RegistrationState>,
	onIntent: (RegistrationIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()

	DismissKeyboardOnDispose()

	when (val current = uiState) {
		RegistrationState.Initial -> RegistrationForm(
			username = UsernameValidationItem(),
			displayName = DisplayNameValidationItem(),
			password = PasswordValidationItem(),
			message = null,
			onIntent = onIntent,
		)

		is RegistrationState.Loading -> RegistrationForm(
			username = current.username,
			displayName = current.displayName,
			password = current.password,
			message = null,
			onIntent = onIntent,
		)

		is RegistrationState.Content -> RegistrationForm(
			username = current.username,
			displayName = current.displayName,
			password = current.password,
			message = null,
			onIntent = onIntent,
		)

		is RegistrationState.Error -> RegistrationForm(
			username = current.username,
			displayName = current.displayName,
			password = current.password,
			message = current.message,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun RegistrationForm(
	username: UsernameValidationItem,
	displayName: DisplayNameValidationItem,
	password: PasswordValidationItem,
	message: Int?,
	onIntent: (RegistrationIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 32.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		MUiDisplayAuthText(text = stringResource(R.string.registration_title))

		MUiBodyRegularText(
			text = stringResource(R.string.registration_hint),
			color = palette.textSecondary,
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = username.data,
				placeholder = stringResource(R.string.registration_login),
				error = stringResource(id = usernameErrorMessage(state = username.validationState)),
				outlined = true,
			),
			onValueChange = { onIntent(RegistrationIntent.ChangeUsername(it)) },
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = displayName.data,
				placeholder = stringResource(R.string.registration_name),
				error = stringResource(id = displayNameErrorMessage(state = displayName.validationState)),
				outlined = true,
			),
			onValueChange = { onIntent(RegistrationIntent.ChangeDisplayName(it)) },
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = password.data,
				placeholder = stringResource(R.string.registration_password),
				error = stringResource(id = passwordErrorMessage(state = password.validationState)),
				secret = true,
				outlined = true,
			),
			onValueChange = { onIntent(RegistrationIntent.ChangePassword(it)) },
		)

		if (message != null) {
			MUiCaptionSmallText(
				text = stringResource(message),
				color = MaterialTheme.colorScheme.error,
			)
		}

		RegistrationActions(onIntent)
	}
}

@Composable
private fun RegistrationActions(onIntent: (RegistrationIntent) -> Unit) {
	MUiButton(
		model = MUiButtonModel(
			style = MUiButtonStyle.Primary,
				label = stringResource(R.string.registration_submit),
		),
		onClick = { onIntent(RegistrationIntent.Submit) },
	)

	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		MUiButton(
			model = MUiButtonModel(
				style = MUiButtonStyle.Text,
				label = stringResource(R.string.registration_have_account),
			),
			onClick = { onIntent(RegistrationIntent.OpenSignIn) },
		)

		HorizontalDivider(color = palette.borderDefault)

		MUiBodyDefaultText(
			text = stringResource(R.string.registration_workspace),
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.clickable { onIntent(RegistrationIntent.OpenWorkspace) }
				.padding(8.dp),
		)
	}
}
