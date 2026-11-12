package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

internal fun List<ChatLine>.days(): Map<String, List<DayLine>> {
	val visible = filter { line -> line.threadRoot == null }

	val marked = visible.mapIndexed { index, line ->
		DayLine(
			line = line,
			continued = grouped(
				previous = visible.getOrNull(index - 1),
				line = line,
			),
		)
	}

	return marked.groupBy { row -> messageDay(row.line.sentAt).orEmpty() }
}

private fun grouped(previous: ChatLine?, line: ChatLine): Boolean {
	if (previous == null || line.service || previous.service) {
		return false
	}

	if (line.mine || previous.mine) {
		return false
	}

	return previous.author == line.author
}
