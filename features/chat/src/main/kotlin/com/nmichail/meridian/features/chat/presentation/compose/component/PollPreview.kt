package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R

internal fun String.showsPoll(): Boolean = startsWith("poll\n") || startsWith("vote\n")

internal fun String.pollPreview(): String? {
	if (startsWith("poll\n")) {
		return removePrefix("poll\n")
			.lineSequence()
			.firstOrNull { line -> line.isNotBlank() && !line.startsWith("quiz\t") }
	}

	if (startsWith("vote\n")) {
		return removePrefix("vote\n").lines().getOrNull(2)?.takeIf { line -> line.isNotBlank() }
	}

	return null
}

@Composable
internal fun PollPreview(text: String, modifier: Modifier = Modifier) {
	val title = text.pollPreview() ?: stringResource(R.string.chat_poll)

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_chat_poll),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(18.dp),
		)
		Text(
			text = title,
			style = MeridianType.captionDefault.copy(fontSize = 15.sp, lineHeight = 20.sp),
			color = palette.textSecondary,
			maxLines = 1,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
internal fun DiscussionTexts(text: String, comment: String) {
	PreviewLine(text)

	if (comment.isNotEmpty()) {
		PreviewLine(comment)
	}
}

@Composable
private fun PreviewLine(text: String) {
	if (text.showsPoll()) {
		PollPreview(text = text)
		return
	}

	Text(
		text = text,
		style = MeridianType.captionDefault,
		color = palette.textSecondary,
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
	)
}
