package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.nmichail.meridian.component.ui.validation.usernameErrorMessage
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiBodyDefaultText
import com.nmichail.meridian.component.ui.text.MUiBodyNameText
import com.nmichail.meridian.component.ui.text.MUiCaptionDefaultText
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.features.groups.R
import com.nmichail.meridian.features.groups.presentation.GroupIntent
import com.nmichail.meridian.features.groups.presentation.GroupState
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.component.ui.R as UiR

@Composable
internal fun GroupScreen(
	state: StateFlow<GroupState>,
	onIntent: (GroupIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	LaunchedEffect(Unit) {
		onIntent(GroupIntent.Load)
	}
	when (val state = uiState) {
		GroupState.Initial -> Unit

		is GroupState.Loading -> GroupBody(
			title = state.title,
			description = state.description,
			login = state.login,
			adding = state.adding,
			members = state.members,
			message = null,
			onIntent = onIntent,
		)

		is GroupState.Content -> GroupBody(
			title = state.title,
			description = state.description,
			login = state.login,
			adding = state.adding,
			members = state.members,
			message = null,
			onIntent = onIntent,
		)

		is GroupState.Error -> GroupBody(
			title = state.title,
			description = state.description,
			login = state.login,
			adding = state.adding,
			members = state.members,
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
			.padding(8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier
				.size(16.dp)
				.clickable { onIntent(GroupIntent.NavigateBack) },
		)

		Text(
			text = stringResource(R.string.group_title),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun GroupBody(
	title: DisplayNameValidationItem,
	description: String,
	login: UsernameValidationItem,
	adding: Boolean,
	members: List<ChatPerson>,
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
				.padding(horizontal = 16.dp, vertical = 16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			GroupPhoto()

			GroupFields(
				title = title,
				description = description,
				onIntent = onIntent,
			)

			MemberBlock(
				login = login,
				adding = adding,
				members = members,
				onIntent = onIntent,
			)
		}

		GroupSubmit(
			message = message,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun GroupPhoto() {
	Row(
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Box(
			modifier = Modifier
				.size(72.dp)
				.clip(CircleShape)
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_group_camera),
				contentDescription = null,
				modifier = Modifier.size(28.dp),
			)
		}

		MUiBodyDefaultText(
			text = stringResource(R.string.group_photo),
			color = palette.accentPrimary,
		)
	}
}

@Composable
private fun GroupFields(
	title: DisplayNameValidationItem,
	description: String,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = title.data,
				label = stringResource(R.string.group_name),
				error = stringResource(id = displayNameErrorMessage(state = title.validationState)),
				outlined = true,
			),
			onValueChange = { onIntent(GroupIntent.ChangeTitle(title = it)) },
		)

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = description,
				label = stringResource(R.string.group_description),
				placeholder = stringResource(R.string.group_description_hint),
				outlined = true,
			),
			onValueChange = { onIntent(GroupIntent.ChangeDescription(description = it)) },
		)
	}
}

@Composable
private fun MemberBlock(
	login: UsernameValidationItem,
	adding: Boolean,
	members: List<ChatPerson>,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		MUiBodyNameText(
			text = stringResource(R.string.group_members, members.size),
		)

		MemberChips(
			members = members,
			onIntent = onIntent,
		)

		AddMembers(
			login = login,
			adding = adding,
			onIntent = onIntent,
		)
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemberChips(
	members: List<ChatPerson>,
	onIntent: (GroupIntent) -> Unit,
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		members.forEach { person ->
			MemberChip(
				person = person,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun MemberChip(
	person: ChatPerson,
	onIntent: (GroupIntent) -> Unit,
) {
	val name = person.displayName.ifBlank { person.username }
	Row(
		modifier = Modifier
			.clip(RoundedCornerShape(18.dp))
			.background(palette.field)
			.padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Box(
			modifier = Modifier
				.size(24.dp)
				.clip(CircleShape)
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = initials(name),
				style = MeridianType.chatMeta,
				color = palette.accentPrimary,
			)
		}

		MUiCaptionDefaultText(text = name)

		Image(
			painter = painterResource(UiR.drawable.ic_close),
			contentDescription = null,
			modifier = Modifier
				.size(14.dp)
				.clickable { onIntent(GroupIntent.RemoveMember(userId = person.userId)) },
		)
	}
}

@Composable
private fun AddMembers(
	login: UsernameValidationItem,
	adding: Boolean,
	onIntent: (GroupIntent) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clickable { onIntent(GroupIntent.OpenMembers) }
				.padding(horizontal = 16.dp, vertical = 14.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Image(
				painter = painterResource(R.drawable.ic_person_add),
				contentDescription = null,
				modifier = Modifier.size(22.dp),
			)

			MUiBodyDefaultText(
				text = stringResource(R.string.group_add),
				color = palette.accentPrimary,
			)
		}

		if (adding) {
			MUiTextField(
				model = MUiTextFieldModel(
					icon = MUiFieldIcon.None,
					value = login.data,
					placeholder = stringResource(R.string.group_member_login),
					error = stringResource(id = usernameErrorMessage(state = login.validationState)),
					outlined = true,
				),
				onValueChange = { onIntent(GroupIntent.ChangeLogin(login = it)) },
				onDone = { onIntent(GroupIntent.AddMember) },
			)
		}
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
				label = stringResource(R.string.group_submit),
			),
			onClick = { onIntent(GroupIntent.Submit) },
			modifier = Modifier.fillMaxWidth(),
		)
	}
}
