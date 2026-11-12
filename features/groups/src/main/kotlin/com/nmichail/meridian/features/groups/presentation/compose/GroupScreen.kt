package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.validation.displayNameErrorMessage
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiCaptionDefaultText
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.features.groups.R
import com.nmichail.meridian.features.groups.presentation.GroupIntent
import com.nmichail.meridian.features.groups.presentation.GroupState
import com.nmichail.meridian.component.ui.R as UiR

@Composable
internal fun GroupScreen(
	state: StateFlow<GroupState>,
	chatId: String,
	title: String,
	description: String,
	editing: Boolean,
	onIntent: (GroupIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()

	LaunchedEffect(editing) {
		if (editing) {
			onIntent(
				GroupIntent.Open(
					chatId = chatId,
					title = title,
					description = description,
				),
			)
		} else {
			onIntent(GroupIntent.Load)
		}
	}

	when (val state = uiState) {
		GroupState.Initial -> Unit
		is GroupState.Loading -> GroupBody(
			title = state.title,
			description = state.description,
			photo = state.photo,
			enabled = false,
			message = null,
			onIntent = onIntent,
		)

		is GroupState.Content -> GroupBody(
			title = state.title,
			description = state.description,
			photo = state.photo,
			enabled = true,
			message = null,
			onIntent = onIntent,
		)

		is GroupState.Error -> GroupBody(
			title = state.title,
			description = state.description,
			photo = "",
			enabled = true,
			message = state.message,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun GroupBar(
	onIntent: (GroupIntent) -> Unit,
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
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier
				.size(16.dp)
				.clickable { onIntent(GroupIntent.NavigateBack) },
		)
	}
}

@Composable
private fun GroupBody(
	title: DisplayNameValidationItem,
	description: String,
	photo: String,
	enabled: Boolean,
	message: Int?,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		GroupBar(onIntent = onIntent)

		Column(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			GroupPhoto(
				title = title.data,
				photo = photo,
				enabled = enabled,
				onPhoto = { bytes, type ->
					onIntent(GroupIntent.InstallPhoto(bytes = bytes, type = type))
				},
			)

			GroupFields(
				title = title,
				description = description,
				onIntent = onIntent,
			)

			GroupOptions()
		}

		GroupSubmit(
			message = message,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun GroupFields(
	title: DisplayNameValidationItem,
	description: String,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = title.data,
				label = stringResource(R.string.group_field_name),
				error = stringResource(id = displayNameErrorMessage(state = title.validationState)),
			),
			onValueChange = { onIntent(GroupIntent.ChangeTitle(title = it)) },
			container = palette.surface,
			stroke = palette.borderDefault,
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = description,
				label = stringResource(R.string.group_field_description),
			),
			onValueChange = { onIntent(GroupIntent.ChangeDescription(description = it)) },
			container = palette.surface,
			stroke = palette.borderDefault,
		)
	}
}

@Composable
private fun GroupOptions() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface),
	) {
		OptionRow(
			icon = R.drawable.ic_group_people,
			label = R.string.group_type,
			value = R.string.group_type_private,
		)

		OptionRow(
			icon = R.drawable.ic_group_link,
			label = R.string.group_invite,
			value = R.string.group_invite_on,
		)
	}
}

@Composable
private fun OptionRow(
	icon: Int,
	label: Int,
	value: Int,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = stringResource(label),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		Text(
			text = stringResource(value),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
		)

		Image(
			painter = painterResource(UiR.drawable.ic_chevron_right),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(18.dp),
		)
	}
}

@Composable
private fun GroupSubmit(
	message: Int?,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		if (message != null) {
			MUiCaptionDefaultText(
				text = stringResource(message),
				color = palette.statusError,
			)
		}

		MUiButton(
			model = MUiButtonModel(
				style = MUiButtonStyle.Primary,
				label = stringResource(R.string.group_save),
			),
			onClick = { onIntent(GroupIntent.Submit) },
			modifier = Modifier.fillMaxWidth(),
		)
	}
}
