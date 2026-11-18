package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

@Composable
internal fun MessageQuote(line: ChatLine, corporate: Boolean, onOpen: (String, String) -> Unit) {
	val author = line.replyAuthor
	val quoted = line.replyText
	if (author.isNullOrBlank() || quoted.isNullOrBlank()) {
		return
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(IntrinsicSize.Min)
			.clickable { onOpen(author, quoted) },
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Box(
			modifier = Modifier
				.width(2.dp)
				.fillMaxHeight()
				.clip(RoundedCornerShape(2.dp))
				.background(palette.accentPrimary),
		)

		Column(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(6.dp),
			) {
				if (corporate) {
					Image(
						painter = painterResource(UiR.drawable.ic_building),
						contentDescription = null,
						colorFilter = ColorFilter.tint(palette.accentPrimary),
						modifier = Modifier.size(14.dp),
					)
				}

				Text(
					text = author,
					style = MeridianType.bodyRegular.copy(fontWeight = FontWeight.SemiBold),
					color = palette.accentPrimary,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			}

			Text(
				text = quoted,
				style = MeridianType.bodyRegular,
				color = palette.textPrimary,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

internal fun List<ChatLine>.quotedId(author: String, text: String, you: String, greeting: String): String? =
	asReversed().firstOrNull { line ->
		val name = line.author?.takeIf { known -> known.isNotBlank() } ?: you
		val body = if (line.id == "hello") greeting else line.text.toPoll()?.question ?: line.text

		!line.deleted && name == author && body == text
	}?.id

internal fun Map<String, List<DayLine>>.quoteIndex(id: String): Int {
	var index = 0

	entries.reversed().forEach { (label, dayLines) ->
		dayLines.asReversed().forEach { row ->
			if (row.line.vote() != null) {
				return@forEach
			}

			if (row.line.id == id) {
				return index
			}

			index++
		}

		if (label.isNotEmpty()) {
			index++
		}
	}

	return -1
}

internal fun quotedScroll(
	lines: List<ChatLine>,
	shown: List<ChatLine>,
	offset: Int,
	you: String,
	greeting: String,
	scroll: (Int) -> Unit,
): (String, String) -> Unit = { author, text ->
	val id = lines.quotedId(author = author, text = text, you = you, greeting = greeting)
	val index = shown.indexOfFirst { line -> line.id == id }

	if (id != null && index >= 0) {
		scroll(index + offset)
	}
}