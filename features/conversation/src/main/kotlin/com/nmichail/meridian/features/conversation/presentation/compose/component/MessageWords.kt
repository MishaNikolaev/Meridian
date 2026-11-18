package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent
import com.nmichail.meridian.features.conversation.presentation.asFile
import com.nmichail.meridian.features.conversation.presentation.mediaType
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

@Composable
internal fun MessageWords(
	line: ChatLine,
	spoken: String,
	photo: Boolean,
	withText: Boolean,
	glyph: Color,
	reserve: Dp,
	mine: Boolean,
	continued: Boolean,
	onOpen: () -> Unit,
	onIntent: (ConversationIntent) -> Unit,
) {
	val poll = if (line.deleted) null else spoken.toPoll()
	val tally = LocalTallies.current[line.id]
	val caption = photo && withText

	Column(
		modifier = Modifier.padding(end = if (photo) 0.dp else reserve),
	) {
		if (poll != null) {
			PollCard(
				question = poll.question,
				description = poll.description,
				options = poll.options,
				counts = tally?.counts.orEmpty(),
				picked = tally?.mine ?: -1,
				correct = poll.correct,
				solution = poll.solution,
				voters = tally?.voters.orEmpty(),
				onPick = { index ->
					onIntent(ConversationIntent.Vote(pollId = line.id, option = index, question = poll.question))
				},
			)
		} else if (photo) {
			MessagePicture(fileId = line.fileId, onOpen = onOpen)
		} else if (line.fileName.isNotEmpty()) {
			FileCard(line = line, onOpen = onOpen)
		}

		if (poll == null && withText) {
			Spoken(
				spoken = spoken,
				glyph = glyph,
				caption = caption,
				mine = mine,
				continued = continued,
				reserve = reserve,
			)
		}
	}
}

@Composable
private fun Spoken(
	spoken: String,
	glyph: Color,
	caption: Boolean,
	mine: Boolean,
	continued: Boolean,
	reserve: Dp,
) {
	val bubble = when {
		!caption -> Modifier
		mine -> outgoingBubble()
		else -> incomingBubble(continued)
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.then(if (caption) Modifier.captionLift() else Modifier)
			.then(bubble)
			.padding(end = if (caption) reserve else 0.dp),
	) {
		MessageGlyphs(text = spoken, color = glyph)
	}
}

private fun Modifier.captionLift(): Modifier = layout { measurable, constraints ->
	val placeable = measurable.measure(constraints)
	val overlap = 14.dp.roundToPx()
	val height = (placeable.height - overlap).coerceAtLeast(0)
	layout(width = placeable.width, height = height) {
		placeable.place(x = 0, y = -overlap)
	}
}

internal fun ChatLine.photoOnly(): Boolean = photo() && (text.isEmpty() || text == fileName)

internal fun ChatLine.photoWithText(): Boolean = photo() && text.isNotEmpty() && text != fileName

private fun ChatLine.photo(): Boolean {
	val image = fileType.mediaType().startsWith("image/") && !fileType.asFile()
	return image && fileId.isNotEmpty() && !deleted
}

@Composable
internal fun incomingBubble(continued: Boolean): Modifier = Modifier
	.clip(
		RoundedCornerShape(
			topStart = 18.dp,
			topEnd = 18.dp,
			bottomEnd = 18.dp,
			bottomStart = if (continued) 4.dp else 18.dp,
		),
	)
	.background(palette.chatIncoming)
	.padding(horizontal = 12.dp, vertical = 8.dp)

@Composable
internal fun outgoingBubble(): Modifier = Modifier
	.clip(
		RoundedCornerShape(
			topStart = 18.dp,
			topEnd = 18.dp,
			bottomStart = 18.dp,
			bottomEnd = 4.dp,
		),
	)
	.background(palette.chatOutgoing)
	.padding(horizontal = 12.dp, vertical = 8.dp)