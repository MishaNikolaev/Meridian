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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.effect.LiveDataEffect
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
import com.nmichail.meridian.features.authorization.workspace.R
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceEvent
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceIntent
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceState

@Composable
fun WorkspaceScreen(
	state: StateFlow<WorkspaceState>,
	onIntent: (WorkspaceIntent) -> Unit,
	event: LiveData<WorkspaceEvent>,
) {
	val uiState by state.collectAsStateWithLifecycle()
	var shown by remember { mutableStateOf<WorkspaceEvent?>(null) }
	var notice by remember { mutableIntStateOf(0) }
	LiveDataEffect(event) { value ->
		notice += 1
		shown = value
	}
	DismissKeyboardOnDispose()
	Box(
		modifier = Modifier
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.safeDrawing),
	) {
		when (val current = uiState) {
			WorkspaceState.Initial -> WorkspaceForm(host = "", email = "", password = "", onIntent = onIntent)

			is WorkspaceState.Loading -> WorkspaceForm(
				host = current.host,
				email = current.email,
				password = current.password,
				onIntent = onIntent,
			)

			is WorkspaceState.Content -> WorkspaceForm(
				host = current.host,
				email = current.email,
				password = current.password,
				onIntent = onIntent,
			)

			is WorkspaceState.Error -> WorkspaceForm(
				host = current.host,
				email = current.email,
				password = current.password,
				onIntent = onIntent,
			)
		}
		val message = shown
		if (message != null) {
			WorkspaceNotice(
				event = message,
				notice = notice,
				onDismiss = { shown = null },
			)
		}
	}
}

@Composable
private fun WorkspaceForm(
	host: String,
	email: String,
	password: String,
	onIntent: (WorkspaceIntent) -> Unit,
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
				label = stringResource(R.string.workspace_host),
				value = host,
			),
			onValueChange = { onIntent(WorkspaceIntent.ChangeHost(it)) },
		)
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.Mail,
				label = stringResource(R.string.workspace_email),
				value = email,
				email = true,
			),
			onValueChange = { onIntent(WorkspaceIntent.ChangeEmail(it)) },
		)
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				label = stringResource(R.string.workspace_password),
				value = password,
				secret = true,
			),
			onValueChange = { onIntent(WorkspaceIntent.ChangePassword(it)) },
		)
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Primary, label = stringResource(R.string.workspace_submit)),
			onClick = { onIntent(WorkspaceIntent.Submit) },
		)
		ExternalAccountLinks(onIntent = onIntent)
	}
}

@Composable
private fun BoxScope.WorkspaceNotice(event: WorkspaceEvent, notice: Int, onDismiss: () -> Unit) {
	val snackbarModifier = Modifier
		.align(Alignment.BottomCenter)
		.padding(horizontal = 16.dp, vertical = 24.dp)
	key(notice) {
		MUiSnackbar(
			model = MUiSnackbarModel(tone = MUiSnackbarTone.Error, message = eventMessage(event)),
			onDismiss = onDismiss,
			modifier = snackbarModifier,
		)
	}
}

@Composable
private fun eventMessage(event: WorkspaceEvent): String = when (event) {
	WorkspaceEvent.NotFound -> stringResource(R.string.workspace_not_found)
	WorkspaceEvent.PasswordLength -> stringResource(UiR.string.validation_error_password_length)
	is WorkspaceEvent.NotHandledError -> stringResource(event.message)
}

@Composable
private fun ExternalAccountLinks(onIntent: (WorkspaceIntent) -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Text, label = stringResource(R.string.workspace_external)),
			onClick = { onIntent(WorkspaceIntent.OpenExternalSignIn) },
		)
	}
}

@Composable
private fun WorkspaceHeading() {
	val secondary = MeridianThemeTokens.palette.textSecondary
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		MUiDisplayAuthText(text = stringResource(R.string.workspace_title))
		MUiBodyRegularText(
			text = stringResource(R.string.workspace_hint),
			color = secondary,
		)
	}
}
