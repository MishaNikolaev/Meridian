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
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.nmichail.meridian.component.ui.swipe.MUiSwipeReply
import com.nmichail.meridian.component.photo.PhotoFace
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
	onQuote: (String, String) -> Unit,
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
		line.text.toPoll()?.question ?: line.text
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
		val deletion = rememberPixelDeletion(line)

		if (deletion.line.mine) {
			Outgoing(
				line = deletion.line,
				deletion = deletion,
				selected = selected,
				reactions = reactions,
				viewers = viewers,
				corporate = corporate,
				comments = comments,
				onReactions = onReactions,
				onQuote = onQuote,
				onIntent = onIntent,
			)
		} else {
			Incoming(
				line = deletion.line,
				deletion = deletion,
				continued = continued,
				selected = selected,
				reactions = reactions,
				viewers = viewers,
				corporate = corporate,
				comments = comments,
				onReactions = onReactions,
				onQuote = onQuote,
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
	deletion: PixelDeletion,
	continued: Boolean,
	selected: Boolean,
	reactions: Set<String>,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onQuote: (String, String) -> Unit,
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

		val photoOnly = line.photoOnly()
		val photoWithText = line.photoWithText()

		DeletingBubble(
			deletion = deletion,
			modifier = Modifier
				.widthIn(max = if (photoOnly || photoWithText) 280.dp else 272.dp)
				.then(if (photoWithText) Modifier.width(280.dp) else Modifier.width(IntrinsicSize.Max))
				.then(if (photoOnly || photoWithText) Modifier else incomingBubble(continued)),
		) {
		Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
			val author = line.author

			if (!author.isNullOrBlank()) {
				MessageAuthor(author = author, corporate = corporate)
			}

			MessageQuote(line, corporate, onQuote)

			MessageText(
				line = line,
				glyph = palette.textPrimary,
				meta = palette.textSecondary,
				checked = false,
				mine = false,
				continued = continued,
				onIntent = onIntent,
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
}

@Composable
private fun Outgoing(
	line: ChatLine,
	deletion: PixelDeletion,
	selected: Boolean,
	reactions: Set<String>,
	viewers: List<ChatReaction>,
	corporate: Boolean,
	comments: Int,
	onReactions: (() -> Unit)?,
	onQuote: (String, String) -> Unit,
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

		val photoOnly = line.photoOnly()
		val photoWithText = line.photoWithText()

		DeletingBubble(
			deletion = deletion,
			modifier = Modifier
				.widthIn(max = if (photoOnly || photoWithText) 280.dp else 272.dp)
				.then(if (photoWithText) Modifier.width(280.dp) else Modifier.width(IntrinsicSize.Max))
				.then(if (photoOnly || photoWithText) Modifier else outgoingBubble()),
		) {
		Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
			MessageQuote(line, corporate, onQuote)

			MessageText(
				line = line,
				glyph = palette.chatOutgoingText,
				meta = palette.chatOutgoingText,
				checked = true,
				mine = true,
				continued = false,
				onIntent = onIntent,
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
	mine: Boolean,
	continued: Boolean,
	onIntent: (ConversationIntent) -> Unit,
) {
	val reserve = if (line.edited && !line.deleted) 112.dp else 58.dp
	val spoken = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text
	val photo = line.photoOnly() || line.photoWithText()
	val withText = spoken.isNotEmpty() && spoken != line.fileName
	val openFile = {
		onIntent(
			ConversationIntent.OpenFile(
				fileId = line.fileId,
				fileName = line.fileName,
				fileType = line.fileType,
			),
		)
	}

	Box(modifier = Modifier.fillMaxWidth()) {
		MessageWords(
			line = line,
			spoken = spoken,
			photo = photo,
			withText = withText,
			glyph = glyph,
			reserve = reserve,
			mine = mine,
			continued = continued,
			onOpen = openFile,
			onIntent = onIntent,
		)

		Meta(
			time = messageClock(line.sentAt),
			color = if (photo && !withText) Color.White else meta,
			checked = checked,
			edited = line.edited && !line.deleted,
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.zIndex(1f)
				.then(
					when {
						photo && !withText -> Modifier
							.padding(end = 8.dp, bottom = 8.dp)
							.clip(RoundedCornerShape(10.dp))
							.background(Color(color = 0x8C000000))
							.padding(horizontal = 6.dp, vertical = 2.dp)
						photo -> Modifier.padding(end = 12.dp, bottom = 8.dp)
						else -> Modifier
					},
				),
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
