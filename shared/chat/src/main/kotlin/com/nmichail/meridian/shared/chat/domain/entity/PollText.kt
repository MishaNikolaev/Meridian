package com.nmichail.meridian.shared.chat.domain.entity

internal fun Poll.toText(): String {
	val clean = options.map { option -> option.trim().replace("\n", " ") }.filter { option -> option.isNotEmpty() }
	val asked = question.trim().replace("\n", " ")
	val about = description.trim().replace("\n", " ").replace("\t", " ")
	val head = buildString {
		append("poll\n")
		append(asked)
		if (about.isNotEmpty()) {
			append("\ndesc\t")
			append(about)
		}
		if (anonymous) {
			append("\nanon")
		}
		append("\n")
		append(clean.joinToString(separator = "\n"))
	}

	if (correct < 0) {
		return head
	}

	val note = solution.trim().replace("\n", " ").replace("\t", " ")

	return "$head\nquiz\t$correct\t$note"
}

internal fun Vote.toText(): String {
	val asked = question.trim().replace("\n", " ")
	val head = "vote\n$pollId\n$option"

	if (asked.isEmpty()) {
		return head
	}

	return "$head\n$asked"
}

internal fun String.toPoll(): Poll? {
	if (!startsWith("poll\n")) {
		return null
	}

	val raw = removePrefix("poll\n").lines()
	val marker = raw.indexOfFirst { line -> line.startsWith("quiz\t") }
	val lines = (if (marker < 0) raw else raw.take(marker)).filter { line -> line.isNotBlank() }

	if (lines.size <= 2) {
		return null
	}

	val quiz = if (marker < 0) emptyList() else raw[marker].split('\t')
	val body = lines.toMutableList()
	val description = if (body.size > 1 && body[1].startsWith("desc\t")) {
		body.removeAt(1).removePrefix("desc\t")
	} else {
		""
	}
	val anonymous = body.removeAll { line -> line == "anon" }

	if (body.size <= 2) {
		return null
	}

	return Poll(
		question = body.first(),
		description = description,
		options = body.drop(1),
		correct = quiz.getOrNull(1)?.toIntOrNull() ?: -1,
		solution = quiz.drop(2).joinToString("\t"),
		anonymous = anonymous,
	)
}

internal fun String.toVote(): Vote? {
	if (!startsWith("vote\n")) {
		return null
	}

	val parts = removePrefix("vote\n").lines()
	val pollId = parts.firstOrNull().orEmpty()
	val option = parts.getOrNull(1)?.toIntOrNull() ?: return null

	if (pollId.isEmpty() || option < 0) {
		return null
	}

	return Vote(
		pollId = pollId,
		option = option,
		question = parts.getOrNull(2).orEmpty(),
	)
}
