package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.validation.usernameErrorMessage
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.groups.R
import com.nmichail.meridian.features.groups.presentation.GroupInfoIntent
import com.nmichail.meridian.features.groups.presentation.GroupMate

private const val MEMBER_PREVIEW = 3

@Composable
internal fun DetailCard() {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface),
	) {
		DetailRow(
			icon = R.drawable.ic_group_camera,
			title = stringResource(R.string.group_media),
			value = "",
		)

		DetailRow(
			icon = R.drawable.ic_group_bell,
			title = stringResource(R.string.group_notifications),
			value = stringResource(R.string.group_notifications_on),
		)

		DetailRow(
			icon = R.drawable.ic_group_pin,
			title = stringResource(R.string.group_pinned),
			value = "",
		)

		DetailRow(
			icon = R.drawable.ic_group_calendar,
			title = stringResource(R.string.group_sprint),
			value = stringResource(R.string.group_sprint_current),
		)
	}
}

@Composable
private fun DetailRow(
	icon: Int,
	title: String,
	value: String,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = title,
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		if (value.isNotEmpty()) {
			Text(
				text = value,
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
			)
		}

		Image(
			painter = painterResource(UiR.drawable.ic_chevron_right),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(18.dp),
		)
	}
}

@Composable
internal fun LeaveRow(
	onIntent: (GroupInfoIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.clickable { onIntent(GroupInfoIntent.Leave) }
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.group_leave),
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
}

@Composable
internal fun MemberCard(
	mates: List<GroupMate>,
	menu: String?,
	expanded: Boolean,
	adding: Boolean,
	login: UsernameValidationItem,
	canEdit: Boolean,
	corporate: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	val visible = if (expanded || mates.size <= MEMBER_PREVIEW) {
		mates
	} else {
		mates.take(n = MEMBER_PREVIEW)
	}

	val known = mates.any { mate -> mate.owner }
	val me = mates.find { mate -> mate.self }
	val canTransfer = me != null && (me.owner || !known)

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface),
	) {
		Text(
			text = if (mates.size == 1) {
				stringResource(R.string.group_members_title)
			} else {
				stringResource(R.string.group_members, mates.size)
			},
			style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
			color = palette.textPrimary,
			modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp),
		)

		visible.forEach { mate ->
			MemberLine(
				mate = mate,
				open = menu == mate.name && mate.name.isNotEmpty(),
				known = known,
				canTransfer = canTransfer && mate.userId.isNotEmpty(),
				corporate = corporate,
				onIntent = onIntent,
			)
		}

		if (!expanded && mates.size > MEMBER_PREVIEW) {
			ShowAll(onIntent = onIntent)
		}

		if (canEdit) {
			if (adding) {
				LoginField(login = login, onIntent = onIntent)
			}

			AddMember(adding = adding, onIntent = onIntent)
		}
	}
}

@Composable
private fun LoginField(
	login: UsernameValidationItem,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	MUiTextField(
		model = MUiTextFieldModel(
			icon = MUiFieldIcon.None,
			value = login.data,
			label = stringResource(R.string.group_member_login),
			error = stringResource(id = usernameErrorMessage(state = login.validationState)),
		),
		onValueChange = { onIntent(GroupInfoIntent.ChangeLogin(login = it)) },
		onDone = { onIntent(GroupInfoIntent.ConfirmAdd) },
		container = palette.field,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}

@Composable
private fun AddMember(
	adding: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable {
				if (adding) {
					onIntent(GroupInfoIntent.ConfirmAdd)
				} else {
					onIntent(GroupInfoIntent.StartAdd)
				}
			}
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(R.drawable.ic_group_people),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.accentPrimary),
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = stringResource(R.string.group_add_member),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
		)
	}
}

@Composable
private fun ShowAll(
	onIntent: (GroupInfoIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onIntent(GroupInfoIntent.Expand) }
			.padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(R.drawable.ic_group_people),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.accentPrimary),
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = stringResource(R.string.group_show_all),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
		)
	}
}

@Composable
private fun MemberLine(
	mate: GroupMate,
	open: Boolean,
	known: Boolean,
	canTransfer: Boolean,
	corporate: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	val name = mate.name

	Box(modifier = Modifier.fillMaxWidth()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.then(
					if (mate.self) {
						Modifier
					} else {
						Modifier.clickable { onIntent(GroupInfoIntent.Member(name = mate.name)) }
					},
				)
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			PhotoFace(
				name = name,
				userId = mate.userId,
				size = 40.dp,
				style = MeridianType.captionDefault,
			)

			Text(
				text = name,
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				modifier = Modifier.weight(1f),
			)

			if (mate.owner || !known && mate.self) {
				RoleBadge(label = R.string.group_owner)
			} else if (mate.admin) {
				RoleBadge(label = R.string.group_admin)
			}
		}

		if (!mate.self) {
			MemberMenu(
				open = open,
				admin = mate.admin,
				canTransfer = canTransfer,
				profile = mate.userId.isNotEmpty(),
				corporate = corporate,
				onIntent = onIntent,
				name = mate.name,
			)
		}
	}
}

@Composable
private fun RowScope.RoleBadge(
	label: Int,
) {
	Text(
		text = stringResource(label),
		style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
		color = palette.accentPrimary,
		modifier = Modifier
			.align(Alignment.CenterVertically)
			.clip(RoundedCornerShape(10.dp))
			.background(palette.accentPrimarySoft)
			.padding(horizontal = 10.dp, vertical = 6.dp),
	)
}

@Composable
private fun MemberMenu(
	open: Boolean,
	admin: Boolean,
	canTransfer: Boolean,
	profile: Boolean,
	corporate: Boolean,
	name: String,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	DropdownMenu(
		expanded = open,
		onDismissRequest = { onIntent(GroupInfoIntent.Dismiss) },
		containerColor = palette.surface,
		shape = RoundedCornerShape(14.dp),
	) {
		if (profile) {
			DropdownMenuItem(
				text = {
					Text(
						text = stringResource(R.string.group_profile),
						style = MeridianType.bodyDefault,
						color = palette.textPrimary,
					)
				},
				onClick = { onIntent(GroupInfoIntent.Profile(name = name, corporate = corporate)) },
			)
		}

		if (!admin) {
			DropdownMenuItem(
				text = {
					Text(
						text = stringResource(R.string.group_make_admin),
						style = MeridianType.bodyDefault,
						color = palette.textPrimary,
					)
				},
				onClick = { onIntent(GroupInfoIntent.MakeAdmin(name = name)) },
			)
		}

		if (canTransfer) {
			DropdownMenuItem(
				text = {
					Text(
						text = stringResource(R.string.group_transfer),
						style = MeridianType.bodyDefault,
						color = palette.textPrimary,
					)
				},
				onClick = { onIntent(GroupInfoIntent.Transfer(name = name)) },
			)
		}

		DropdownMenuItem(
			text = {
				Text(
					text = stringResource(R.string.group_remove),
					style = MeridianType.bodyDefault,
					color = palette.statusError,
				)
			},
			onClick = { onIntent(GroupInfoIntent.Remove(name = name)) },
		)
	}
}
