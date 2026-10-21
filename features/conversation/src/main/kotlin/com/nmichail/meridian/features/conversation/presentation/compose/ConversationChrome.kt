package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.conversation.R

private const val HEADER_AVATAR = 40
private const val AVATAR_CORNER = 14
private const val BACK_ICON = 16
private const val ACTION_ICON = 22
private const val INITIALS = 2

@Composable
internal fun ConversationHeader(title: String, note: String, bot: Boolean, onBack: () -> Unit) {
	val palette = MeridianThemeTokens.palette
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(palette.borderDefault, Offset(0f, size.height), Offset(size.width, size.height), stroke)
			}
			.padding(start = 10.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		HeaderGlyph(R.drawable.ic_back, BACK_ICON, onBack)
		PeerMark(title, bot)
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
		HeaderGlyph(R.drawable.ic_phone, ACTION_ICON, onClick = null)
		HeaderGlyph(R.drawable.ic_more, ACTION_ICON, onClick = null)
	}
}

@Composable
internal fun EditBar(text: String, onClose: () -> Unit) {
	val palette = MeridianThemeTokens.palette
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(palette.borderDefault, Offset.Zero, Offset(size.width, 0f), stroke)
			}
			.padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		HeaderGlyph(R.drawable.ic_edit, ACTION_ICON, onClick = null)
		Column(modifier = Modifier.weight(1f)) {
			Text(text = stringResource(R.string.conversation_edit), style = MeridianType.captionSmall, color = palette.accentPrimary)
			Text(
				text = text,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
		HeaderGlyph(R.drawable.ic_close, ACTION_ICON, onClose)
	}
}

@Composable
private fun PeerMark(title: String, bot: Boolean) {
	val palette = MeridianThemeTokens.palette
	val shape = if (bot) CircleShape else RoundedCornerShape(AVATAR_CORNER.dp)
	val fill = if (bot) palette.accentPrimary else palette.accentPrimarySoft
	val ink = if (bot) palette.textOnAccent else palette.accentPrimary
	Box(
		modifier = Modifier
			.size(HEADER_AVATAR.dp)
			.clip(shape)
			.background(fill)
			.then(if (bot) Modifier else Modifier.border(1.dp, palette.borderDefault, shape)),
		contentAlignment = Alignment.Center,
	) {
		Text(text = mark(title), style = MeridianType.captionDefault, color = ink)
	}
}

@Composable
private fun HeaderGlyph(icon: Int, size: Int, onClick: (() -> Unit)?) {
	val modifier = Modifier.size(size.dp)
	Image(
		painter = painterResource(icon),
		contentDescription = null,
		modifier = if (onClick == null) modifier else modifier.clickable(onClick = onClick),
	)
}

private fun mark(title: String): String {
	val parts = title.trim().split(" ").filter { it.isNotEmpty() }
	val raw = if (parts.size >= INITIALS) parts[0].take(1) + parts[1].take(1) else title.take(INITIALS)
	return raw.uppercase()
}
