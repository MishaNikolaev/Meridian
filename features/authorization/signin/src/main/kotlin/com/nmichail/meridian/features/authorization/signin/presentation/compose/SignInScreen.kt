package com.nmichail.meridian.features.authorization.signin.presentation.compose

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
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.compose.passwordErrorMessage
import com.nmichail.meridian.component.ui.compose.usernameErrorMessage
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.theme.palette
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyDefaultText
import com.nmichail.meridian.component.ui.text.MUiCaptionSmallText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.features.authorization.signin.R
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.features.authorization.signin.presentation.SignInIntent
import com.nmichail.meridian.features.authorization.signin.presentation.SignInState

@Composable
fun SignInScreen(
	state: StateFlow<SignInState>,
	onIntent: (SignInIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	DismissKeyboardOnDispose()
	when (val current = uiState) {
		SignInState.Initial -> SignInForm(
			username = UsernameValidationItem(),
			password = PasswordValidationItem(),
			message = null,
			onIntent = onIntent,
		)

		is SignInState.Loading -> SignInForm(
			username = current.username,
			password = current.password,
			message = null,
			onIntent = onIntent,
		)

		is SignInState.Content -> SignInForm(
			username = current.username,
			password = current.password,
			message = null,
			onIntent = onIntent,
		)

		is SignInState.Error -> SignInForm(
			username = current.username,
			password = current.password,
			message = current.message,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun SignInForm(
	username: UsernameValidationItem,
	password: PasswordValidationItem,
	message: Int?,
	onIntent: (SignInIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 32.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		MUiDisplayAuthText(text = stringResource(R.string.sign_in_title))
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = username.data,
				placeholder = stringResource(R.string.sign_in_login),
				error = stringResource(id = usernameErrorMessage(state = username.validationState)),
			),
			onValueChange = { onIntent(SignInIntent.ChangeUsername(it)) },
		)
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = password.data,
				placeholder = stringResource(R.string.sign_in_password),
				error = stringResource(id = passwordErrorMessage(state = password.validationState)),
				secret = true,
			),
			onValueChange = { onIntent(SignInIntent.ChangePassword(it)) },
		)
		if (message != null) {
			MUiCaptionSmallText(
				text = stringResource(message),
				color = MaterialTheme.colorScheme.error,
			)
		}
		MUiButton(
			model = MUiButtonModel(
				style = MUiButtonStyle.Primary,
				label = stringResource(R.string.sign_in_submit),
			),
			onClick = { onIntent(SignInIntent.Submit) },
		)
		SignInLinks(onIntent = onIntent)
	}
}

@Composable
private fun SignInLinks(onIntent: (SignInIntent) -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Text, label = stringResource(R.string.sign_in_create_account)),
			onClick = { onIntent(SignInIntent.OpenRegistration) },
		)
		HorizontalDivider(color = palette.borderDefault)
		MUiBodyDefaultText(
			text = stringResource(R.string.sign_in_workspace),
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.clickable { onIntent(SignInIntent.OpenWorkspace) }
				.padding(8.dp),
		)
	}
}
