package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.messageClock
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

private const val BUBBLE = 272
private const val RADIUS = 18
private const val TAIL = 4
private const val AVATAR = 32
private const val PILL = 12
private const val INITIALS = 2

@Composable
internal fun DayPill(label: String) {
	Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
		Text(
			text = label,
			style = MeridianType.captionSmall,
			color = MeridianThemeTokens.palette.textSecondary,
			modifier = Modifier
				.clip(RoundedCornerShape(PILL.dp))
				.background(MeridianThemeTokens.palette.surface)
				.padding(horizontal = 10.dp, vertical = 3.dp),
		)
	}
}

@Composable
internal fun MessageBubble(line: ChatLine, continued: Boolean, onEdit: (ChatLine) -> Unit) {
	if (line.mine) {
		Outgoing(line, onEdit)
	} else {
		Incoming(line, continued)
	}
}

@Composable
private fun Incoming(line: ChatLine, continued: Boolean) {
	val palette = MeridianThemeTokens.palette
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.Bottom,
	) {
		AuthorMark(line.author.orEmpty())
		Column(
			modifier = Modifier
				.widthIn(max = BUBBLE.dp)
				.clip(incomingShape(continued))
				.background(palette.chatIncoming)
				.padding(horizontal = 12.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			val author = line.author
			if (!author.isNullOrBlank()) {
				Text(text = author, style = MeridianType.captionDefault, color = nameColor(author))
			}
			Text(
				text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
				style = MeridianType.chatMessage,
				color = palette.textPrimary,
			)
			Meta(messageClock(line.sentAt), palette.textSecondary, checked = false)
		}
	}
}

@Composable
private fun Outgoing(line: ChatLine, onEdit: (ChatLine) -> Unit) {
	val palette = MeridianThemeTokens.palette
	Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
		Column(
			modifier = Modifier
				.widthIn(max = BUBBLE.dp)
				.clip(outgoingShape())
				.clickable { onEdit(line) }
				.background(palette.chatOutgoing)
				.padding(horizontal = 12.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Text(
				text = if (line.id == "hello") stringResource(R.string.conversation_greeting) else line.text,
				style = MeridianType.chatMessage,
				color = palette.textOnAccent,
			)
			Meta(messageClock(line.sentAt), palette.textOnAccent, checked = true)
		}
	}
}

@Composable
private fun Meta(time: String, color: Color, checked: Boolean) {
	Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
		Text(text = time, style = MeridianType.chatMeta, color = color)
		if (checked) {
			Image(painter = painterResource(R.drawable.ic_done_all), contentDescription = null)
		}
	}
}

@Composable
private fun AuthorMark(author: String) {
	Box(
		modifier = Modifier.size(AVATAR.dp).clip(CircleShape).background(nameColor(author)),
		contentAlignment = Alignment.Center,
	) {
		Text(text = mark(author), style = MeridianType.captionSmall, color = MeridianThemeTokens.palette.textOnAccent)
	}
}

private fun incomingShape(continued: Boolean) = RoundedCornerShape(
	topStart = RADIUS.dp,
	topEnd = RADIUS.dp,
	bottomEnd = RADIUS.dp,
	bottomStart = if (continued) TAIL.dp else RADIUS.dp,
)

private fun outgoingShape() = RoundedCornerShape(
	topStart = RADIUS.dp,
	topEnd = RADIUS.dp,
	bottomStart = RADIUS.dp,
	bottomEnd = TAIL.dp,
)

@Composable
private fun nameColor(author: String): Color {
	val palette = MeridianThemeTokens.palette
	val colors = listOf(palette.statusSuccess, Color(color = 0xFF7C3AED), palette.accentPrimary)
	return colors[author.hashCode().mod(colors.size)]
}

private fun mark(title: String): String {
	val parts = title.trim().split(" ").filter { it.isNotEmpty() }
	val raw = if (parts.size >= INITIALS) parts[0].take(1) + parts[1].take(1) else title.take(INITIALS)
	return raw.uppercase()
}
