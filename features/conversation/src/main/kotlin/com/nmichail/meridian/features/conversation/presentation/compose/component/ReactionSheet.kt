package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.sheet.MUiSheet
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
internal fun ReactionSheet(
	open: Boolean,
	viewers: List<ChatReaction>,
	onDismiss: () -> Unit,
) {
	MUiSheet(
		open = open,
		onDismiss = onDismiss,
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(max = 520.dp)
				.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
				.background(palette.surface)
				.windowInsetsPadding(WindowInsets.navigationBars)
				.padding(top = 10.dp, bottom = 16.dp),
		) {
			Box(
				modifier = Modifier.fillMaxWidth(),
				contentAlignment = Alignment.Center,
			) {
				Box(
					modifier = Modifier
						.size(width = 40.dp, height = 4.dp)
						.clip(RoundedCornerShape(2.dp))
						.background(palette.borderStrong),
				)
			}

			Text(
				text = stringResource(R.string.conversation_reactions, viewers.size),
				style = MeridianType.titleCard,
				color = palette.textPrimary,
				modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp),
			)

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.verticalScroll(rememberScrollState()),
			) {
				viewers.forEach { viewer ->
					ReactionPerson(viewer)
				}
			}
		}
	}
}

@Composable
private fun ReactionPerson(viewer: ChatReaction) {
	val name = viewer.name

	val reaction = reactionGlyph(viewer.reaction)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		ReactionMark(name = name)

		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(1.dp),
		) {
			Text(
				text = name,
				style = MeridianType.bodyName,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			val sentAt = viewer.sentAt

			if (!sentAt.isNullOrBlank()) {
				Text(
					text = reactionWhen(sentAt),
					style = MeridianType.captionDefault,
					color = palette.textSecondary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}

		if (reaction.isNotBlank()) {
			Text(
				text = reaction,
				style = MeridianType.chatEmoji,
			)
		}
	}
}

@Composable
private fun ReactionMark(name: String) {
	if (name.isBlank()) {
		Box(
			modifier = Modifier
				.size(40.dp)
				.clip(CircleShape)
				.background(palette.field),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_tab_profile),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textSecondary),
				modifier = Modifier.size(24.dp),
			)
		}

		return
	}

	PhotoFace(
		name = name,
		size = 40.dp,
		style = MeridianType.bodySmall,
	)
}

@Composable
private fun reactionWhen(iso: String): String {
	val clock = messageClock(iso)

	if (reactionToday(iso)) {
		return stringResource(R.string.conversation_reaction_today, clock)
	}

	return stringResource(R.string.conversation_reaction_when, messageDay(iso).orEmpty(), clock)
}
