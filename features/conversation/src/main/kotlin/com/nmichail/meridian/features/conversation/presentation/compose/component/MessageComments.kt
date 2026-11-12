package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
internal fun MessageComments(
	count: Int,
	mine: Boolean,
	lineId: String,
	onIntent: (ConversationIntent) -> Unit,
) {
	val label = palette.accentPrimary

	val line = select(
		active = mine,
		on = palette.chatOutgoingText.copy(alpha = 0.35f),
		off = palette.borderDefault,
	)

	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.drawBehind {
					val extra = 12.dp.toPx()

					val stroke = 1.dp.toPx()

					drawLine(
						line,
						Offset(-extra, stroke / 2),
						Offset(size.width + extra, stroke / 2),
						stroke,
					)
				},
		) {
			Text(
				text = pluralStringResource(R.plurals.conversation_comment_count, count, count),
				style = MeridianType.bodyDefault,
				color = label,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						onIntent(ConversationIntent.StartDiscussion(lineId = lineId))
					}
					.padding(top = 8.dp, bottom = 2.dp),
			)
		}
	}
}

@Composable
internal fun MessageReactions(
	viewers: List<ChatReaction>,
	reactions: Set<String>,
	mine: Boolean,
	onReactions: (() -> Unit)?,
) {
	val groups = reactionGroups(viewers, reactions)

	if (groups.isEmpty() && onReactions == null) {
		return
	}

	val color = select(
		active = mine,
		on = palette.chatOutgoingText,
		off = palette.textPrimary,
	)

	val fill = select(
		active = mine,
		on = Color(color = 0xFF6E9AD6),
		off = palette.accentPrimarySoft,
	)

	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		ReactionRow(
			groups = groups,
			color = color,
			fill = fill,
			mine = mine,
			onReactions = onReactions,
		)
	}
}

@Composable
private fun ReactionRow(
	groups: List<Pair<String, Int>>,
	color: Color,
	fill: Color,
	mine: Boolean,
	onReactions: (() -> Unit)?,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
		verticalAlignment = Alignment.CenterVertically,
	) {
		groups.forEach { (reaction, count) ->
			ReactionChip(
				reaction = reaction,
				count = count,
				mine = mine,
				color = color,
				fill = fill,
			)
		}

		if (onReactions != null) {
			Box(
				modifier = Modifier
					.height(26.dp)
					.clip(CircleShape)
					.background(palette.field)
					.clickable(onClick = onReactions)
					.padding(horizontal = 8.dp),
				contentAlignment = Alignment.Center,
			) {
				Image(
					painter = painterResource(R.drawable.ic_more_horizontal),
					contentDescription = stringResource(R.string.conversation_message_info),
					modifier = Modifier.size(width = 14.dp, height = 4.dp),
				)
			}
		}
	}
}

@Composable
private fun ReactionChip(
	reaction: String,
	count: Int,
	mine: Boolean,
	color: Color,
	fill: Color,
) {
	val emoji = MeridianType.chatEmoji

	Row(
		modifier = Modifier
			.height(26.dp)
			.clip(CircleShape)
			.background(fill)
			.padding(horizontal = 6.dp),
		horizontalArrangement = Arrangement.spacedBy(2.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = reaction,
			style = emoji.copy(
				fontSize = 16.sp,
				lineHeight = 16.sp,
				textAlign = TextAlign.Center,
				lineHeightStyle = LineHeightStyle(
					alignment = LineHeightStyle.Alignment.Center,
					trim = LineHeightStyle.Trim.Both,
				),
				platformStyle = PlatformTextStyle(includeFontPadding = false),
			),
		)

		Text(
			text = count.toString(),
			style = MeridianType.captionDefault,
			color = select(
				active = mine,
				on = palette.textOnAccent,
				off = color,
			),
		)
	}
}

private fun reactionGroups(
	viewers: List<ChatReaction>,
	reactions: Set<String>,
): List<Pair<String, Int>> {
	val grouped = viewers.groupBy { viewer -> reactionGlyph(viewer.reaction) }
		.filter { (glyph, _) -> glyph.isNotBlank() }
		.map { (glyph, people) -> glyph to people.size }

	if (grouped.isNotEmpty()) {
		return grouped
	}

	return reactions.map { glyph -> glyph to 1 }
}
