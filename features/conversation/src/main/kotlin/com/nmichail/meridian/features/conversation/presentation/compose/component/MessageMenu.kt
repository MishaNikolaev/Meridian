package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.component.ui.emoji.MUiReactionBoard
import com.nmichail.meridian.component.ui.emoji.MessageReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

private const val REACTION_ROW = 6

@Composable
internal fun MessageMenuHost(
	lines: List<ChatLine>,
	menuLineId: String?,
	pinnedId: String?,
	reactions: List<MessageReaction>,
	selected: Set<String>,
	onIntent: (ConversationIntent) -> Unit,
) {
	val line = lines.firstOrNull { it.id == menuLineId } ?: return

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Black.copy(alpha = 0.42f))
			.clickable(
				interactionSource = remember { MutableInteractionSource() },
				indication = null,
				onClick = { onIntent(ConversationIntent.DismissMessageMenu) },
			),
		contentAlignment = Alignment.Center,
	) {
		Column(
			modifier = Modifier
				.padding(horizontal = 24.dp)
				.pointerInput(line.id) {
					detectTapGestures(
						onTap = { Unit },
					)
				},
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			ReactionPicker(
				lineId = line.id,
				reactions = reactions,
				selected = selected,
				onIntent = onIntent,
			)

			ActionMenu(
				line = line,
				pinned = line.id == pinnedId,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
internal fun SelectionBar(count: Int, onIntent: (ConversationIntent) -> Unit) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(60.dp)
			.background(palette.surface)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			}
			.padding(horizontal = 12.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_close),
			contentDescription = null,
			modifier = Modifier
				.size(16.dp)
				.clickable { onIntent(ConversationIntent.ClearSelection) },
		)

		Text(
			text = stringResource(R.string.conversation_selected, count),
			style = MeridianType.titleSection,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		Image(
			painter = painterResource(R.drawable.ic_message_delete),
			contentDescription = null,
			modifier = Modifier
				.size(26.dp)
				.clickable { onIntent(ConversationIntent.DeleteSelection) },
		)

		ForwardButton()
	}
}

@Composable
internal fun SelectionCheck(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.size(24.dp)
			.border(width = 2.dp, color = Color.White, shape = CircleShape)
			.background(palette.accentPrimary, CircleShape),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_message_check),
			contentDescription = null,
			modifier = Modifier.size(14.dp),
		)
	}
}

internal fun Modifier.pressMessage(
	lineId: String,
	onIntent: (ConversationIntent) -> Unit,
): Modifier = pointerInput(lineId) {
	detectTapGestures(
		onTap = {
			onIntent(ConversationIntent.OpenMessage(lineId = lineId))
		},
		onLongPress = {
			onIntent(ConversationIntent.StartSelection(lineId = lineId))
		},
	)
}

@Composable
private fun ReactionPicker(
	lineId: String,
	reactions: List<MessageReaction>,
	selected: Set<String>,
	onIntent: (ConversationIntent) -> Unit,
) {
	var expanded by remember { mutableStateOf(false) }

	val shape = RoundedCornerShape(28.dp)

	val top = reactions.take(REACTION_ROW)

	val rest = reactions.drop(REACTION_ROW)

	Column(
		modifier = Modifier
			.shadow(elevation = 8.dp, shape = shape)
			.clip(shape)
			.background(palette.surface)
			.padding(horizontal = 10.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			top.forEach { reaction ->
				ReactionChoice(
					lineId = lineId,
					reaction = reaction,
					selected = reaction.glyph in selected,
					onIntent = onIntent,
				)
			}

			Box(
				modifier = Modifier
					.size(30.dp)
					.clip(CircleShape)
					.background(palette.field)
					.clickable { expanded = !expanded },
				contentAlignment = Alignment.Center,
			) {
				Image(
					painter = painterResource(R.drawable.ic_reaction_more),
					contentDescription = stringResource(R.string.conversation_more_reactions),
					modifier = Modifier.size(18.dp),
				)
			}
		}

		if (expanded) {
			MUiReactionBoard(
				reactions = rest,
				selected = selected,
				onPick = { glyph ->
					onIntent(
						ConversationIntent.PickReaction(
							lineId = lineId,
							reaction = glyph,
						),
					)
				},
			)
		}
	}
}

@Composable
private fun ReactionChoice(
	lineId: String,
	reaction: MessageReaction,
	selected: Boolean,
	onIntent: (ConversationIntent) -> Unit,
) {
	Text(
		text = reaction.glyph,
		style = MeridianType.reactionMenu,
		modifier = Modifier
			.clip(CircleShape)
			.background(
				select(
					active = selected,
					on = palette.accentPrimarySoft,
					off = Color.Transparent,
				),
			)
			.clickable {
			onIntent(
				ConversationIntent.PickReaction(
					lineId = lineId,
					reaction = reaction.glyph,
				),
			)
		},
	)
}

@Composable
private fun ActionMenu(
	line: ChatLine,
	pinned: Boolean,
	onIntent: (ConversationIntent) -> Unit,
) {
	val clipboard = LocalClipboard.current

	val text = if (line.id == "hello") {
		stringResource(R.string.conversation_greeting)
	} else {
		line.text
	}

	val author = line.author
		?.takeIf { it.isNotBlank() }
		?: stringResource(R.string.conversation_you)

	Column(
		modifier = Modifier
			.width(236.dp)
			.shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.padding(vertical = 6.dp),
	) {
		MessageAction.entries.forEach { action ->
			if (action == MessageAction.EDIT && (!line.mine || line.deleted)) return@forEach

			val label = if (action == MessageAction.PIN && pinned) {
				R.string.conversation_unpin
			} else {
				action.label
			}

			ActionRow(
				action = action,
				label = label,
				onClick = {
					action.choose(
						line = line,
						text = text,
						author = author,
						clipboard = clipboard,
						onIntent = onIntent,
					)
				},
			)
		}
	}
}

@Composable
private fun ActionRow(
	action: MessageAction,
	label: Int,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(action.icon),
			contentDescription = null,
			modifier = Modifier.size(20.dp),
		)

		Text(
			text = stringResource(label),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun ForwardButton() {
	Row(
		modifier = Modifier
			.clip(RoundedCornerShape(18.dp))
			.background(palette.accentButton)
			.padding(start = 14.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(R.drawable.ic_message_forward),
			contentDescription = null,
			modifier = Modifier.size(18.dp),
			colorFilter = ColorFilter.tint(palette.textOnAccent),
		)

		Text(
			text = stringResource(R.string.conversation_forward),
			style = MeridianType.labelBadge,
			color = palette.textOnAccent,
		)
	}
}