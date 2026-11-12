package com.nmichail.meridian.features.conversation.presentation.compose.component

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.initials.markColor
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent

@Composable
internal fun ConversationBar(
	title: String,
	note: String,
	bot: Boolean,
	pinned: String?,
	personId: String,
	chatId: String,
	onIntent: (ConversationIntent) -> Unit,
) {
	val border = palette.borderDefault

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			},
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			BarIcon(UiR.drawable.ic_back, size = 16) {
				onIntent(ConversationIntent.NavigateBack)
			}

			Row(
				modifier = Modifier
					.weight(1f)
					.clickable(enabled = personId.isNotEmpty() || !bot) { onIntent(ConversationIntent.OpenPerson) },
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				PeerMark(title = title, bot = bot, personId = personId, chatId = chatId)

				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = title,
						style = MeridianType.bodyName,
						color = palette.textPrimary,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)

					if (note.isNotEmpty()) {
						Text(text = note, style = MeridianType.captionSmall, color = palette.textSecondary, maxLines = 1)
					}
				}
			}

			BarIcon(R.drawable.ic_phone, size = 22, onClick = null)

			BarIcon(R.drawable.ic_more, size = 18, onClick = null)
		}

		if (!pinned.isNullOrBlank()) {
			PinnedLine(text = pinned)
		}
	}
}

@Composable
private fun PinnedLine(text: String) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 86.dp, end = 12.dp, bottom = 10.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(R.drawable.ic_message_pin),
			contentDescription = null,
			modifier = Modifier.size(16.dp),
			colorFilter = ColorFilter.tint(palette.accentPrimary),
		)

		Text(
			text = text,
			style = MeridianType.captionDefault,
			color = palette.textPrimary,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
		)
	}
}

@Composable
internal fun EditBar(text: String, onIntent: (ConversationIntent) -> Unit) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset.Zero,
					Offset(size.width, 0f),
					stroke,
				)
			}
			.padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		BarIcon(R.drawable.ic_edit, size = 22, onClick = null)

		Column(modifier = Modifier.weight(1f)) {
			Text(text = stringResource(R.string.conversation_edit),
				style = MeridianType.captionSmall,
				color = palette.accentPrimary
			)

			Text(
				text = text,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}

		BarIcon(UiR.drawable.ic_close, size = 22) { onIntent(ConversationIntent.CancelEdit) }
	}
}

@Composable
internal fun ReplyBar(author: String, text: String, onIntent: (ConversationIntent) -> Unit) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()

				drawLine(
					border,
					Offset.Zero,
					Offset(size.width, 0f),
					stroke,
				)
			}
			.padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_message_reply),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.accentPrimary),
			modifier = Modifier.size(22.dp),
		)

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = stringResource(R.string.conversation_reply_author, shortName(author)),
				style = MeridianType.captionSmall,
				color = palette.accentPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)

			Text(
				text = text,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}

		BarIcon(UiR.drawable.ic_close, size = 16) { onIntent(ConversationIntent.CancelReply) }
	}
}

private fun shortName(author: String): String {
	val parts = author.trim().split(" ").filter { it.isNotEmpty() }

	return if (parts.size > 2) {
		parts[0] + " " + parts[1]
	} else {
		author
	}
}

@Composable
private fun PeerMark(title: String, bot: Boolean, personId: String, chatId: String) {
	if (!bot && personId.isNotEmpty()) {
		PhotoFace(
			name = title,
			userId = personId,
			size = 40.dp,
			corner = 14.dp,
			style = MeridianType.captionDefault,
		)

		return
	}

	if (!bot) {
		PhotoFace(
			name = title,
			chatId = chatId,
			size = 40.dp,
			corner = 14.dp,
			style = MeridianType.captionDefault,
		)

		return
	}

	val shape = if (bot) CircleShape else RoundedCornerShape(14.dp)

	Box(
		modifier = Modifier
			.size(40.dp)
			.clip(shape)
			.background(markColor(title)),
		contentAlignment = Alignment.Center,
	) {
		Text(text = initials(title), style = MeridianType.captionDefault, color = palette.textOnAccent)
	}
}

@Composable
private fun BarIcon(icon: Int, size: Int, onClick: (() -> Unit)?) {
	val modifier = Modifier.size(size.dp)

	Image(
		painter = painterResource(icon),
		contentDescription = null,
		modifier = if (onClick == null) modifier else modifier.clickable(onClick = onClick),
	)
}