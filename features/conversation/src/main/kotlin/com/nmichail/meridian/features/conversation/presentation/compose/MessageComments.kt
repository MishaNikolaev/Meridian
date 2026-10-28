package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.reactionGlyph
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
internal fun MessageComments(
	count: Int,
	mine: Boolean,
	lineId: String,
	onIntent: (ConversationIntent) -> Unit,
) {
	val label = palette.accentPrimary
	val line = if (mine) palette.chatOutgoingText.copy(alpha = 0.35f) else palette.borderDefault
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
	reaction: String?,
	mine: Boolean,
	onReactions: (() -> Unit)?,
) {
	val groups = reactionGroups(viewers, reaction)
	if (groups.isEmpty() && onReactions == null) {
		return
	}
	val color = if (mine) palette.chatOutgoingText else palette.textPrimary
	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		ReactionRow(
			groups = groups,
			color = color,
			onReactions = onReactions,
		)
	}
}

@Composable
private fun ReactionRow(
	groups: List<Pair<String, Int>>,
	color: Color,
	onReactions: (() -> Unit)?,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
		verticalAlignment = Alignment.CenterVertically,
	) {
		groups.forEach { (reaction, count) ->
			Row(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					text = reaction,
					style = MeridianType.chatEmoji,
				)
				Text(
					text = count.toString(),
					style = MeridianType.bodyDefault,
					color = color,
				)
			}
		}
		if (onReactions != null) {
			Box(
				modifier = Modifier
					.clip(RoundedCornerShape(12.dp))
					.background(palette.field)
					.clickable(onClick = onReactions)
					.padding(horizontal = 8.dp, vertical = 6.dp),
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

private fun reactionGroups(
	viewers: List<ChatReaction>,
	reaction: String?,
): List<Pair<String, Int>> {
	val grouped = viewers.groupBy { viewer -> reactionGlyph(viewer.reaction) }
		.filter { (glyph, _) -> glyph.isNotBlank() }
		.map { (glyph, people) -> glyph to people.size }
	if (grouped.isNotEmpty()) {
		return grouped
	}
	if (reaction == null) {
		return emptyList()
	}
	return listOf(reaction to 1)
}
