@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.nmichail.meridian.features.authorization.workspace.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbar
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarModel
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarTone
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyRegularText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceStore
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceViewModel
import com.nmichail.meridian.features.authorization.workspace.presentation.enteredEmail
import com.nmichail.meridian.features.authorization.workspace.presentation.enteredHost
import com.nmichail.meridian.features.authorization.workspace.presentation.enteredPassword
import com.nmichail.meridian.features.authorization.workspace.presentation.failureMessage

@Composable
fun WorkspaceScreen(viewModel: WorkspaceViewModel) {
	val store = viewModel.store
	val state by store.stateFlow.collectAsStateWithLifecycle(initialValue = store.state)
	DismissKeyboardOnDispose()
	Box(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.safeDrawing),
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 24.dp)
				.padding(top = 40.dp, bottom = 24.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			WorkspaceHeading()
			Spacer(modifier = Modifier.height(12.dp))
			MUiTextField(
				model = MUiTextFieldModel(
					icon = MUiFieldIcon.Building,
					label = "Адрес сервера",
					value = state.enteredHost(),
				),
				onValueChange = { store.accept(WorkspaceStore.Intent.ChangeHost(it)) },
			)
			MUiTextField(
				model = MUiTextFieldModel(
					icon = MUiFieldIcon.Mail,
					label = "Корпоративная почта",
					value = state.enteredEmail(),
					email = true,
				),
				onValueChange = { store.accept(WorkspaceStore.Intent.ChangeEmail(it)) },
			)
			MUiTextField(
				model = MUiTextFieldModel(
					icon = MUiFieldIcon.None,
					label = "Пароль",
					value = state.enteredPassword(),
					secret = true,
				),
				onValueChange = { store.accept(WorkspaceStore.Intent.ChangePassword(it)) },
			)
			MUiButton(
				model = MUiButtonModel(style = MUiButtonStyle.Primary, label = "Войти"),
				onClick = { store.accept(WorkspaceStore.Intent.Submit) },
			)
			ExternalAccountLinks(store = store)
		}
		WorkspaceNotice(
			message = state.failureMessage(),
			onDismiss = { store.accept(WorkspaceStore.Intent.DismissNotice) },
		)
	}
}

@Composable
private fun BoxScope.WorkspaceNotice(message: String?, onDismiss: () -> Unit) {
	if (message != null) {
		MUiSnackbar(
			model = MUiSnackbarModel(tone = MUiSnackbarTone.Error, message = message),
			onDismiss = onDismiss,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(horizontal = 16.dp, vertical = 24.dp),
		)
	}
}

@Composable
private fun ExternalAccountLinks(store: WorkspaceStore) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Text, label = "Войти во внешний аккаунт"),
			onClick = { store.accept(WorkspaceStore.Intent.OpenExternalSignIn) },
		)
	}
}

@Composable
private fun WorkspaceHeading() {
	val secondary = MeridianThemeTokens.palette.textSecondary
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		MUiDisplayAuthText(text = "Вход в рабочее пространство")
		MUiBodyRegularText(
			text = "Укажите сервер компании, корпоративную почту и пароль",
			color = secondary,
		)
	}
}
