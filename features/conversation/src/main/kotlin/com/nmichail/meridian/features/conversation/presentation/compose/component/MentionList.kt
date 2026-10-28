package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.Mention

@Composable
internal fun MentionList(
	mentions: List<Mention>,
	everyone: Boolean,
	members: Int,
	onPick: (String) -> Unit,
) {

	if (mentions.isEmpty() && !everyone) {
		return
	}

	Column(
		modifier = Modifier
			.padding(horizontal = 10.dp, vertical = 6.dp)
			.shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
			.clip(RoundedCornerShape(18.dp))
			.background(palette.surface)
			.heightIn(max = 240.dp)
			.verticalScroll(rememberScrollState())
			.padding(vertical = 6.dp),
	) {
		mentions.forEachIndexed { index, mention ->
			MentionRow(
				title = mention.title,
				note = mention.note,
				marked = index == 0,
				initials = mention.initials,
				onClick = { onPick(mention.token) },
			)
		}

		if (everyone) {
			AllRow(
				members = members,
				marked = mentions.isEmpty(),
				onClick = { onPick("all") },
			)
		}
	}
}

@Composable
private fun MentionRow(
	title: String,
	note: String,
	marked: Boolean,
	initials: String,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(if (marked) palette.field else palette.surface)
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.clip(CircleShape)
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Text(text = initials,
				style = MeridianType.bodyName,
				color = palette.accentPrimary
			)
		}

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = title,
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			Text(
				text = note,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@Composable
private fun AllRow(members: Int, marked: Boolean, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(if (marked) palette.field else palette.surface)
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.clip(CircleShape)
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(com.nmichail.meridian.component.ui.R.drawable.ic_tab_people),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.accentPrimary),
				modifier = Modifier.size(22.dp),
			)
		}

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = stringResource(R.string.conversation_mention_all),
				style = MeridianType.bodyName,
				color = palette.textPrimary,
			)

			Text(
				text = stringResource(R.string.conversation_mention_everyone, members),
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
			)
		}
	}
}