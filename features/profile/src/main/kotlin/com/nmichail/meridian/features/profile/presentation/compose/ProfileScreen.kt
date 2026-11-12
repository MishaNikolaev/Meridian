package com.nmichail.meridian.features.profile.presentation.compose

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.dialog.MUiDialog
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType
import com.nmichail.meridian.component.ui.error.MUiErrorInPlace
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.SuccessSnackBarEffect
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
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
		is ProfileState.Error                       -> ProfileBody(
			name = null,
			email = "",
			position = "",
			department = "",
			host = "",
			status = "",
			draft = null,
			photo = "",
			installing = false,
			notice = false,
			message = current.message,
			onIntent = onIntent,
		)

		is ProfileState.Content                     -> ProfileBody(
			name = current.name,
			email = current.email,
			position = current.position,
			department = current.department,
			host = current.host,
			status = current.status,
			draft = current.statusDraft,
			photo = current.photo,
			installing = current.installing,
			notice = current.notice,
			message = null,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun ProfileBody(
	name: String?,
	email: String,
	position: String,
	department: String,
	host: String,
	status: String,
	draft: String?,
	photo: String,
	installing: Boolean,
	notice: Boolean,
	message: Int?,
	onIntent: (ProfileIntent) -> Unit,
) {
	val snackBarHostState = remember { SnackbarHostState() }

	val saved = stringResource(R.string.profile_photo_saved)

	SuccessSnackBarEffect(
		show = notice,
		message = saved,
		snackBarHostState = snackBarHostState,
		onShown = { onIntent(ProfileIntent.DismissNotice) },
	)

	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(palette.page),
		) {
			ProfileList(
				name = name,
				email = email,
				position = position,
				department = department,
				host = host,
				status = status,
				draft = draft,
				photo = photo,
				installing = installing,
				message = message,
				onIntent = onIntent,
			)
		}

		MUiSnackbarHost(
			snackBarHostState = snackBarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(horizontal = 16.dp, vertical = 24.dp),
		)
	}
}

@Composable
private fun ColumnScope.ProfileList(
	name: String?,
	email: String,
	position: String,
	department: String,
	host: String,
	status: String,
	draft: String?,
	photo: String,
	installing: Boolean,
	message: Int?,
	onIntent: (ProfileIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.weight(1f)
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
			.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		ProfileHeader()

		if (name != null) {
			IdentityCard(
				name = name,
				position = position,
				host = host,
				status = status,
				draft = draft,
				photo = photo,
				installing = installing,
				onIntent = onIntent,
			)

			CorporateFacts(
				host = host,
				department = department,
				position = position,
				email = email,
			)
		}

		if (message != null) {
			MUiErrorInPlace(
				message = stringResource(message),
				retry = stringResource(UiR.string.error_retry),
				onRetry = { onIntent(ProfileIntent.LoadInitialData) },
			)
		}

		AppearanceBlock(version = appVersion())

		LogoutRow(onIntent = onIntent)
	}
}

@Composable
private fun ProfileHeader() {
	Text(
		text = stringResource(R.string.profile_title),
		style = MeridianType.titleScreen,
		color = palette.textPrimary,
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
	)
}

@Composable
private fun LogoutRow(onIntent: (ProfileIntent) -> Unit) {
	var confirm by remember { mutableStateOf(false) }

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.clickable { confirm = true }
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.profile_logout),
			style = MeridianType.bodyDefault,
			color = palette.statusError,
			modifier = Modifier.weight(1f),
		)

		Image(
			painter = painterResource(UiR.drawable.ic_chevron_right),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.statusError),
			modifier = Modifier.size(18.dp),
		)
	}

	if (confirm) {
		LogoutDialog(
			onDismiss = { confirm = false },
			onConfirm = {
				confirm = false

				onIntent(ProfileIntent.Logout)
			},
		)
	}
}

@Composable
private fun LogoutDialog(
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	MUiDialog(
		model = MUiDialogModel(
			type = MUiDialogType.Basic,
			title = stringResource(R.string.profile_logout_title),
			message = stringResource(R.string.profile_logout_message),
			cancel = stringResource(R.string.profile_logout_cancel),
			confirm = stringResource(R.string.profile_logout_confirm),
			options = emptyList(),
			selectedOption = 0,
			fieldLabel = "",
			fieldValue = "",
		),
		onDismiss = onDismiss,
		onCancel = onDismiss,
		onConfirm = onConfirm,
		onOptionSelected = {},
	)
}

@Composable
private fun appVersion(): String {
	val context = LocalContext.current

	return remember(context) { context.packageVersion() }
}

private fun Context.packageVersion(): String {
	val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		packageManager.getPackageInfo(
			packageName,
			PackageManager.PackageInfoFlags.of(0),
		)
	} else {
		@Suppress("DEPRECATION")
		packageManager.getPackageInfo(packageName, 0)
	}

	return info.versionName.orEmpty()
}