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
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.groups.R
import com.nmichail.meridian.features.groups.presentation.GroupInfoIntent

@Composable
internal fun GroupActions(
	more: Boolean,
	canEdit: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		ActionTile(
			icon = R.drawable.ic_group_bell,
			label = R.string.group_mute,
			onClick = null,
			menu = null,
		)

		ActionTile(
			icon = UiR.drawable.ic_search,
			label = R.string.group_search,
			onClick = null,
			menu = null,
		)

		ActionTile(
			icon = R.drawable.ic_group_phone,
			label = R.string.group_call,
			onClick = null,
			menu = null,
		)

		ActionTile(
			icon = R.drawable.ic_group_more,
			label = R.string.group_more,
			onClick = { onIntent(GroupInfoIntent.More) },
			menu = {
				MoreMenu(
					open = more,
					canEdit = canEdit,
					onIntent = onIntent,
				)
			},
		)
	}
}

@Composable
private fun RowScope.ActionTile(
	icon: Int,
	label: Int,
	onClick: (() -> Unit)?,
	menu: (@Composable () -> Unit)?,
) {
	Column(
		modifier = Modifier
			.weight(1f)
			.clip(RoundedCornerShape(16.dp))
			.background(palette.surface)
			.then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
			.padding(top = 12.dp, bottom = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Box(contentAlignment = Alignment.TopEnd) {
			Image(
				painter = painterResource(icon),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier.size(22.dp),
			)

			menu?.invoke()
		}

		Text(
			text = stringResource(label),
			style = MeridianType.captionSmall,
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun MoreMenu(
	open: Boolean,
	canEdit: Boolean,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	DropdownMenu(
		expanded = open,
		onDismissRequest = { onIntent(GroupInfoIntent.Dismiss) },
		containerColor = palette.surface,
		shape = RoundedCornerShape(14.dp),
	) {
		if (canEdit) {
			MoreItem(
				label = R.string.group_menu_edit,
				onClick = { onIntent(GroupInfoIntent.Edit) },
			)
		}

		MoreItem(
			label = R.string.group_type,
			onClick = { onIntent(GroupInfoIntent.Dismiss) },
		)

		MoreItem(
			label = R.string.group_invite,
			onClick = { onIntent(GroupInfoIntent.Dismiss) },
		)
	}
}

@Composable
private fun MoreItem(
	label: Int,
	onClick: () -> Unit,
) {
	DropdownMenuItem(
		text = {
			Text(
				text = stringResource(label),
				style = MeridianType.bodyDefault,
				color = palette.textPrimary,
			)
		},
		onClick = onClick,
	)
}
