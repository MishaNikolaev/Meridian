package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.features.conversation.presentation.compose.component.messageDay
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

data class DayLine(
	val line: ChatLine,
	val continued: Boolean,
)

internal fun List<ChatLine>.days(): Map<String, List<DayLine>> {
	val visible = filter { line -> line.threadRoot == null }
	val marked = visible.mapIndexed { index, line ->
		DayLine(
			line = line,
			continued = continues(
				previous = visible.getOrNull(index - 1),
				line = line,
			),
		)
	}
	return marked.groupBy { row -> messageDay(row.line.sentAt).orEmpty() }
}

private fun continues(previous: ChatLine?, line: ChatLine): Boolean {
	if (previous == null || line.mine || previous.mine) {
		return false
	}
	return previous.author == line.author
}
