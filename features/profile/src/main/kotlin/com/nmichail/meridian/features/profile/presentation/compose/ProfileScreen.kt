package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.error.MUiErrorInPlace
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText
import com.nmichail.meridian.features.profile.R
import com.nmichail.meridian.features.profile.presentation.ProfileIntent
import com.nmichail.meridian.features.profile.presentation.ProfileState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileScreen(
	state: StateFlow<ProfileState>,
	onIntent: (ProfileIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		onIntent(ProfileIntent.LoadInitialData)
	}

	when (val current = uiState) {
		ProfileState.Initial, ProfileState.Loading -> Unit

		is ProfileState.Error                      -> ProfileColumn(
			name = null,
			email = null,
			message = current.message,
			onIntent = onIntent,
		)

		is ProfileState.Content                    -> ProfileColumn(
			name = current.name,
			email = current.email,
			message = null,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun ProfileColumn(
	name: String?,
	email: String?,
	message: Int?,
	onIntent: (ProfileIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		MUiTitleScreenText(text = stringResource(R.string.profile_title))

		if (name != null && email != null) {
			Text(
				text = name,
				modifier = Modifier.padding(top = 24.dp),
			)

			Text(
				text = email,
				modifier = Modifier.padding(top = 8.dp),
			)
		}
		if (message != null) {
			MUiErrorInPlace(
				message = stringResource(message),
				retry = stringResource(UiR.string.error_retry),
				onRetry = { onIntent(ProfileIntent.LoadInitialData) },
				modifier = Modifier.padding(top = 24.dp),
			)
		}

		MUiButton(
			model = MUiButtonModel(
				style = MUiButtonStyle.Danger,
				label = stringResource(R.string.profile_logout),
			),
			onClick = { onIntent(ProfileIntent.Logout) },
			modifier = Modifier
				.padding(top = 24.dp)
				.fillMaxWidth(),
		)
	}
}