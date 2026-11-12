package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.error.MUiErrorInPlace
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.showErrorSnackBar
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.groups.R
import com.nmichail.meridian.features.groups.presentation.GroupInfoIntent
import com.nmichail.meridian.features.groups.presentation.GroupInfoState
import com.nmichail.meridian.features.groups.presentation.GroupMate

@Composable
internal fun GroupInfoScreen(
	chatId: String,
	title: String,
	members: List<String>,
	corporate: Boolean,
	state: StateFlow<GroupInfoState>,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		onIntent(
			GroupInfoIntent.Load(
				chatId = chatId,
				title = title,
				members = members,
			),
		)
	}

	when (val current = uiState) {
		GroupInfoState.Initial -> Unit

		is GroupInfoState.Content -> Box(modifier = Modifier.fillMaxSize()) {
			GroupInfoBody(
				chatId = chatId,
				title = current.title,
				description = current.description,
				mates = current.mates,
				menu = current.menu,
				more = current.more,
				expanded = current.expanded,
				adding = current.adding,
				login = current.login,
				canEdit = current.canEdit,
				corporate = corporate,
				onIntent = onIntent,
			)

			GroupAlerts(
				refused = current.refused,
				failed = current.failed,
				denied = current.denied,
			)
		}

		is GroupInfoState.Error -> Box(modifier = Modifier.fillMaxSize()) {
			MUiErrorInPlace(
				message = stringResource(current.message),
				retry = stringResource(UiR.string.error_retry),
				back = stringResource(UiR.string.error_back),
				onRetry = {
					onIntent(GroupInfoIntent.Load(chatId = chatId, title = title, members = members))
				},
				onBack = { onIntent(GroupInfoIntent.Back) },
				modifier = Modifier.align(Alignment.Center),
			)
		}
	}
}

@Composable
private fun GroupInfoBar(
	canEdit: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			}
			.padding(horizontal = 16.dp, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier
				.size(16.dp)
				.clickable { onIntent(GroupInfoIntent.Back) },
		)

		Box(modifier = Modifier.weight(1f))

		if (canEdit) {
			Image(
				painter = painterResource(R.drawable.ic_group_edit),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier
					.size(16.dp)
					.clickable { onIntent(GroupInfoIntent.Edit) },
			)
		}
	}
}

@Composable
private fun GroupInfoBody(
	chatId: String,
	title: String,
	description: String,
	mates: List<GroupMate>,
	menu: String?,
	more: Boolean,
	expanded: Boolean,
	adding: Boolean,
	login: UsernameValidationItem,
	canEdit: Boolean,
	corporate: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		GroupInfoBar(
			canEdit = canEdit,
			onIntent = onIntent,
		)

		Column(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			GroupHead(
				chatId = chatId,
				title = title,
				count = mates.size,
			)

			if (description.isNotEmpty()) {
				DescriptionCard(description = description)
			}

			GroupActions(
				more = more,
				canEdit = canEdit,
				onIntent = onIntent,
			)

			DetailCard()

			MemberCard(
				mates = mates,
				menu = menu,
				expanded = expanded,
				adding = adding,
				login = login,
				canEdit = canEdit,
				corporate = corporate,
				onIntent = onIntent,
			)

			LeaveRow(onIntent = onIntent)
		}
	}
}

@Composable
private fun GroupHead(
	chatId: String,
	title: String,
	count: Int,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		PhotoFace(
			name = title,
			chatId = chatId,
			size = 88.dp,
			corner = 14.dp,
			style = MeridianType.titleCard,
		)

		Text(
			text = title,
			style = MeridianType.titleCard,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)

		Text(
			text = pluralStringResource(R.plurals.group_people, count, count),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
		)
	}
}

@Composable
private fun BoxScope.GroupAlerts(
	refused: Boolean,
	failed: Boolean,
	denied: Int,
) {
	val scope = rememberCoroutineScope()

	val snackBarHostState = remember { SnackbarHostState() }

	val leave = stringResource(R.string.group_leave_owner)

	val transfer = stringResource(R.string.group_transfer_failed)

	val owner = stringResource(R.string.group_remove_owner)

	LaunchedEffect(refused) {
		if (refused) {
			scope.showErrorSnackBar(snackBarHostState = snackBarHostState, message = leave)
		}
	}

	LaunchedEffect(failed) {
		if (failed) {
			scope.showErrorSnackBar(snackBarHostState = snackBarHostState, message = transfer)
		}
	}

	LaunchedEffect(denied) {
		if (denied > 0) {
			scope.showErrorSnackBar(snackBarHostState = snackBarHostState, message = owner)
		}
	}

	MUiSnackbarHost(
		snackBarHostState = snackBarHostState,
		modifier = Modifier
			.align(Alignment.BottomCenter)
			.padding(horizontal = 16.dp, vertical = 24.dp),
	)
}

@Composable
private fun DescriptionCard(description: String) {
	Text(
		text = description,
		style = MeridianType.bodySmall,
		color = palette.textPrimary,
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.padding(horizontal = 16.dp, vertical = 14.dp),
	)
}