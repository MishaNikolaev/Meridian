package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
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
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
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
	reactions: Set<String>,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	if (line.service) {
		ServiceNote(text = line.text)
		return
	}

	val author = line.author
		?.takeIf { it.isNotBlank() }
		?: stringResource(R.string.conversation_you)

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
				reactions = reactions,
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
				reactions = reactions,
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
private fun ServiceNote(text: String) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = text,
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.clip(RoundedCornerShape(12.dp))
				.background(palette.field)
				.padding(horizontal = 10.dp, vertical = 4.dp),
		)
	}
}

@Composable
private fun Incoming(
	line: ChatLine,
	continued: Boolean,
	selected: Boolean,
	reactions: Set<String>,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				select(
					active = selected,
					on = palette.accentPrimary.copy(alpha = 0.14f),
					off = Color.Transparent,
				),
			)
			.padding(horizontal = 10.dp).pressMessage(line.id, onIntent),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.Bottom,
	) {
		Box {
			AuthorMark(line)

			if (selected) SelectionCheck(modifier = Modifier.align(Alignment.BottomCenter))
		}

		Column(
			modifier = Modifier
				.widthIn(max = 272.dp)
				.width(IntrinsicSize.Max)
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
				MessageAuthor(author = author, corporate = corporate)
			}

			MessageQuote(line, corporate)

			MessageText(
				line = line,
				glyph = palette.textPrimary,
				meta = palette.textSecondary,
				checked = false,
			)

			ReactionsAndComments(
				line = line,
				viewers = viewers,
				reactions = reactions,
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
	reactions: Set<String>,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				select(
					active = selected,
					on = palette.accentPrimary.copy(alpha = 0.14f),
					off = Color.Transparent,
				),
			)
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
				.width(IntrinsicSize.Max)
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

			MessageText(
				line = line,
				glyph = palette.chatOutgoingText,
				meta = palette.chatOutgoingText,
				checked = true,
			)

			ReactionsAndComments(
				line = line,
				viewers = viewers,
				reactions = reactions,
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
	reactions: Set<String>,
	comments: Int,
	mine: Boolean,
	onReactions: (() -> Unit)?,
	onIntent: (ConversationIntent) -> Unit,
) {
	if (!line.deleted && (viewers.isNotEmpty() || reactions.isNotEmpty())) {
		MessageReactions(
			viewers = viewers,
			reactions = reactions,
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
private fun MessageText(
	line: ChatLine,
	glyph: Color,
	meta: Color,
	checked: Boolean,
) {
	val reserve = if (line.edited && !line.deleted) 112.dp else 58.dp

	Box(modifier = Modifier.fillMaxWidth()) {
		Box(modifier = Modifier.padding(end = reserve)) {
			MessageGlyphs(
				text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
				color = glyph,
			)
		}

		Meta(
			time = messageClock(line.sentAt),
			color = meta,
			checked = checked,
			edited = line.edited && !line.deleted,
			modifier = Modifier.align(Alignment.BottomEnd),
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
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
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
private fun AuthorMark(line: ChatLine) {
	PhotoFace(
		name = line.author.orEmpty(),
		userId = line.authorId,
		size = 32.dp,
		style = MeridianType.captionSmall,
	)
}
