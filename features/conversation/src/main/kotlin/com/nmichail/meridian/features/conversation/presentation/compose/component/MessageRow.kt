package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.nmichail.meridian.component.ui.swipe.MUiSwipeReply
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

@Composable
internal fun DayPill(label: String) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 4.dp, bottom = 10.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = label,
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
			modifier = Modifier
				.clip(RoundedCornerShape(12.dp))
				.background(palette.surface)
				.padding(horizontal = 10.dp, vertical = 3.dp),
		)
	}
}

@Composable
internal fun MessageRow(
	line: ChatLine,
	continued: Boolean,
	selected: Boolean,
	reaction: String?,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	val author = line.author?.takeIf { it.isNotBlank() } ?: stringResource(R.string.conversation_you)
	val quoted = if (line.id == "hello") {
		stringResource(R.string.conversation_greeting)
	} else {
		line.text
	}

	MUiSwipeReply(
		enabled = !line.deleted,
		onReply = {
			onIntent(
				ConversationIntent.StartReply(
					author = author,
					text = quoted,
				),
			)
		},
	) {
		if (line.mine) {
			Outgoing(
				line = line,
				selected = selected,
				reaction = reaction,
				viewers = viewers,
				corporate = corporate,
				comments = comments,
				onReactions = onReactions,
				onIntent = onIntent,
			)
		} else {
			Incoming(
				line = line,
				continued = continued,
				selected = selected,
				reaction = reaction,
				viewers = viewers,
				corporate = corporate,
				comments = comments,
				onReactions = onReactions,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun Incoming(
	line: ChatLine,
	continued: Boolean,
	selected: Boolean,
	reaction: String?,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(if (selected) palette.accentPrimary.copy(alpha = 0.14f) else Color.Transparent)
			.padding(horizontal = 10.dp).pressMessage(line.id, onIntent),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.Bottom,
	) {
		Box {
			AuthorMark(line.author.orEmpty())
			if (selected) SelectionCheck(modifier = Modifier.align(Alignment.BottomCenter))
		}

		Column(
			modifier = Modifier
				.widthIn(max = 272.dp)
				.clip(
					RoundedCornerShape(
						topStart = 18.dp,
						topEnd = 18.dp,
						bottomEnd = 18.dp,
						bottomStart = if (continued) 4.dp else 18.dp,
					),
				)
				.background(palette.chatIncoming)
				.padding(horizontal = 12.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			val author = line.author

			if (!author.isNullOrBlank()) {
				MessageAuthor(
					author = author,
					corporate = corporate,
				)
			}
			MessageQuote(line, corporate)

			MessageGlyphs(
				text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
				color = palette.textPrimary,
			)

			Meta(
				time = messageClock(line.sentAt),
				color = palette.textSecondary,
				checked = false,
				edited = line.edited && !line.deleted,
			)

			ReactionsAndComments(
				line = line,
				viewers = viewers,
				reaction = reaction,
				comments = comments,
				mine = false,
				onReactions = onReactions,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun Outgoing(
	line: ChatLine,
	selected: Boolean,
	reaction: String?,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(if (selected) palette.accentPrimary.copy(alpha = 0.14f) else Color.Transparent)
			.padding(horizontal = 10.dp)
			.pressMessage(line.id, onIntent),
		verticalAlignment = Alignment.Bottom,
	) {
		if (selected) {
			SelectionCheck()
		}

		Spacer(modifier = Modifier.weight(1f))

		Column(
			modifier = Modifier
				.widthIn(max = 272.dp)
				.clip(
					RoundedCornerShape(
						topStart = 18.dp,
						topEnd = 18.dp,
						bottomStart = 18.dp,
						bottomEnd = 4.dp,
					),
				)
				.background(palette.chatOutgoing)
				.padding(horizontal = 12.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			MessageQuote(line, corporate)

			MessageGlyphs(
				text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
				color = palette.chatOutgoingText,
			)

			Meta(
				time = messageClock(line.sentAt),
				color = palette.chatOutgoingText,
				checked = true,
				edited = line.edited && !line.deleted,
			)

			ReactionsAndComments(
				line = line,
				viewers = viewers,
				reaction = reaction,
				comments = comments,
				mine = true,
				onReactions = onReactions,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun ReactionsAndComments(
	line: ChatLine,
	viewers: List<ChatReaction>,
	reaction: String?,
	comments: Int,
	mine: Boolean,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	if (!line.deleted && (viewers.isNotEmpty() || reaction != null)) {
		MessageReactions(
			viewers = viewers,
			reaction = reaction,
			mine = mine,
			onReactions = onReactions,
		)
	}

	if (comments > 0) {
		MessageComments(
			count = comments,
			mine = mine,
			lineId = line.id,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun MessageAuthor(author: String, corporate: Boolean) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		if (corporate) {
			Image(
				painter = painterResource(UiR.drawable.ic_building),
				contentDescription = null,
				modifier = Modifier.size(14.dp),
			)
		}

		Text(
			text = author,
			style = MeridianType.bodyRegular.copy(fontWeight = FontWeight.SemiBold),
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun Meta(
	time: String,
	color: Color,
	checked: Boolean,
	edited: Boolean,
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (edited) {
			Text(
				text = stringResource(R.string.conversation_edited),
				style = MeridianType.chatMeta,
				color = color,
			)
		}

		Text(
			text = time,
			style = MeridianType.chatMeta,
			color = color,
		)

		if (checked) {
			Image(
				painter = painterResource(R.drawable.ic_done_all),
				contentDescription = null,
				modifier = Modifier.size(14.dp),
				colorFilter = ColorFilter.tint(color),
			)
		}
	}
}

@Composable
private fun AuthorMark(author: String) {
	Box(
		modifier = Modifier.size(32.dp).clip(CircleShape).background(nameColor(author)),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = initials(author),
			style = MeridianType.captionSmall,
			color = palette.textOnAccent
		)
	}
}

@Composable
private fun nameColor(author: String): Color {
	val colors = listOf(palette.statusSuccess, Color(color = 0xFF7C3AED), palette.accentPrimary)
	return colors[author.hashCode().mod(colors.size)]
}