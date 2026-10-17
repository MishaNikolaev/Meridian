@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.nmichail.meridian.features.authorization.signin.presentation.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.compose.passwordErrorMessage
import com.nmichail.meridian.component.ui.compose.usernameErrorMessage
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyDefaultText
import com.nmichail.meridian.component.ui.text.MUiCaptionSmallText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.features.authorization.signin.presentation.SignInStore
import com.nmichail.meridian.features.authorization.signin.presentation.enteredPassword
import com.nmichail.meridian.features.authorization.signin.presentation.failureMessage
import com.nmichail.meridian.features.authorization.signin.presentation.passwordItem
import com.nmichail.meridian.features.authorization.signin.presentation.usernameItem
import com.nmichail.meridian.features.authorization.signin.presentation.SignInViewModel

@Composable
fun SignInScreen(viewModel: SignInViewModel) {
	val store = viewModel.store
	val state by store.stateFlow.collectAsStateWithLifecycle(initialValue = store.state)
	DismissKeyboardOnDispose()
	Column(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.safeDrawing)
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 32.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		MUiDisplayAuthText(text = "Вход")
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = state.usernameItem().data,
				placeholder = "Логин",
				error = stringResource(id = usernameErrorMessage(state = state.usernameItem().validationState)),
			),
			onValueChange = { store.accept(SignInStore.Intent.ChangeUsername(it)) },
		)
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = state.enteredPassword(),
				placeholder = "Пароль",
				error = stringResource(id = passwordErrorMessage(state = state.passwordItem().validationState)),
				secret = true,
			),
			onValueChange = { store.accept(SignInStore.Intent.ChangePassword(it)) },
		)
		state.failureMessage()?.let { message ->
			MUiCaptionSmallText(
				text = message,
				color = MaterialTheme.colorScheme.error,
			)
		}
		MUiButton(
			model = MUiButtonModel(
				style = MUiButtonStyle.Primary,
				label = "Войти",
			),
			onClick = {
				store.accept(
					SignInStore.Intent.Submit,
				)
			},
		)
		SignInLinks(store)
	}
}

@Composable
private fun SignInLinks(store: SignInStore) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Text, label = "Создать аккаунт"),
			onClick = { store.accept(SignInStore.Intent.OpenRegistration) },
		)
		HorizontalDivider(color = MeridianThemeTokens.palette.borderDefault)
		MUiBodyDefaultText(
			text = "Вход в рабочее пространство",
			color = MeridianThemeTokens.palette.textPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.clickable { store.accept(SignInStore.Intent.OpenWorkspace) }
				.padding(8.dp),
		)
	}
}
