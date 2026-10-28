package com.nmichail.meridian.features.authorization.workspace.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.theme.palette
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.effect.DismissKeyboardOnDispose
import com.nmichail.meridian.component.ui.effect.LiveDataEffect
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.showErrorSnackBar
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyRegularText
import com.nmichail.meridian.component.ui.text.MUiDisplayAuthText
import com.nmichail.meridian.core.network.R as NetworkR
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
	val snackBarHostState = remember { SnackbarHostState() }

	EventObserver(
		event = event,
		snackBarHostState = snackBarHostState,
	)
	DismissKeyboardOnDispose()
	Box(
		modifier = Modifier
			.fillMaxSize(),
	) {
		WorkspaceForm(
			host = when (val current = uiState) {
				WorkspaceState.Initial    -> ""
				is WorkspaceState.Loading -> current.host
				is WorkspaceState.Content -> current.host
				is WorkspaceState.Error   -> current.host
			},
			email = when (val current = uiState) {
				WorkspaceState.Initial    -> ""
				is WorkspaceState.Loading -> current.email
				is WorkspaceState.Content -> current.email
				is WorkspaceState.Error   -> current.email
			},
			password = when (val current = uiState) {
				WorkspaceState.Initial    -> ""
				is WorkspaceState.Loading -> current.password
				is WorkspaceState.Content -> current.password
				is WorkspaceState.Error   -> current.password
			},
			onIntent = onIntent,
		)
		MUiSnackbarHost(
			snackBarHostState = snackBarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(horizontal = 16.dp, vertical = 24.dp),
		)
	}
}

@Composable
private fun EventObserver(
	event: LiveData<WorkspaceEvent>,
	snackBarHostState: SnackbarHostState,
) {
	val context = LocalContext.current
	LiveDataEffect(event) { value ->
		showErrorSnackBar(
			snackBarHostState = snackBarHostState,
			message = context.getString(value.messageId()),
		)
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
				outlined = true,
			),
			onValueChange = { onIntent(WorkspaceIntent.ChangeHost(it)) },
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.Mail,
				label = stringResource(R.string.workspace_email),
				value = email,
				email = true,
				outlined = true,
			),
			onValueChange = { onIntent(WorkspaceIntent.ChangeEmail(it)) },
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				label = stringResource(R.string.workspace_password),
				value = password,
				secret = true,
				outlined = true,
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

private fun WorkspaceEvent.messageId(): Int = when (this) {
	WorkspaceEvent.NotFound        -> R.string.workspace_not_found
	WorkspaceEvent.PasswordLength  -> UiR.string.validation_error_password_length
	WorkspaceEvent.WrongPassword   -> NetworkR.string.error_password_mismatch
	WorkspaceEvent.NoConnection    -> NetworkR.string.error_no_connection
	WorkspaceEvent.NotHandledError -> NetworkR.string.error_unknown
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
	val secondary = palette.textSecondary
	Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
		MUiDisplayAuthText(text = stringResource(R.string.workspace_title))
		MUiBodyRegularText(
			text = stringResource(R.string.workspace_hint),
			color = secondary,
		)
	}
}