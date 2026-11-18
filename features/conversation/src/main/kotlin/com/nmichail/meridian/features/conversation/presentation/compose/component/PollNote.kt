package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.runtime.staticCompositionLocalOf
import com.nmichail.meridian.features.conversation.presentation.ConversationState
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.CoroutineScope

internal val LocalTallies = staticCompositionLocalOf { emptyMap<String, PollVotes>() }

internal val LocalPollVotes = staticCompositionLocalOf<(List<String>, List<PollVoter>) -> Unit> { { _, _ -> } }

internal fun pollBody(
	question: String,
	description: String,
	options: List<String>,
	correct: Int,
	solution: String,
): String {
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
		append("\n")
		append(clean.joinToString(separator = "\n"))
	}

	if (correct < 0) {
		return head
	}

	val note = solution.trim().replace("\n", " ").replace("\t", " ")

	return "$head\nquiz\t$correct\t$note"
}

internal fun voteBody(pollId: String, option: Int, question: String): String {
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

	if (body.size <= 2) {
		return null
	}

	return Poll(
		question = body.first(),
		description = description,
		options = body.drop(1),
		correct = quiz.getOrNull(1)?.toIntOrNull() ?: -1,
		solution = quiz.drop(2).joinToString("\t"),
	)
}

internal fun ChatLine.vote(): Pair<String, Int>? {
	if (!text.startsWith("vote\n")) {
		return null
	}

	val parts = text.removePrefix("vote\n").lines()
	val pollId = parts.firstOrNull().orEmpty()
	val option = parts.getOrNull(1)?.toIntOrNull() ?: return null

	if (pollId.isEmpty() || option < 0) {
		return null
	}

	return pollId to option
}

internal fun note(
	current: ConversationState.Content?,
	text: String,
	send: SendMessageUseCase,
	read: () -> ConversationState,
	publish: (ConversationState) -> Unit,
	reload: (String) -> Unit,
	run: (suspend CoroutineScope.() -> Unit) -> Unit,
) {
	if (current == null || text.isEmpty()) {
		return
	}

	if (current.sending || current.bot) {
		return
	}

	publish(current.copy(sending = true))

	run {
		send(current.chatId, text, "", "", current.rootId.orEmpty(), "", "", "", emptyList())

		val shown = read() as? ConversationState.Content ?: return@run

		publish(shown.copy(sending = false))
		reload(current.chatId)
	}
}

internal fun List<ChatLine>.tallies(): Map<String, PollVotes> {
	val grouped = mapNotNull { line ->
		val vote = line.vote() ?: return@mapNotNull null

		vote.first to (vote.second to line)
	}.groupBy { vote -> vote.first }

	return grouped.mapValues { (_, votes) ->
		val latest = votes.map { vote -> vote.second }.groupBy { vote -> vote.second.authorId }.map { (_, items) -> items.last() }
		val counts = MutableList(latest.maxOf { vote -> vote.first } + 1) { 0 }

		latest.forEach { vote -> counts[vote.first] = counts[vote.first] + 1 }

		PollVotes(
			counts = counts,
			mine = latest.lastOrNull { vote -> vote.second.mine }?.first ?: -1,
			voters = latest.map { vote ->
				val line = vote.second
				val who = line.author?.takeIf { name -> name.isNotBlank() } ?: if (line.mine) "Вы" else ""
				PollVoter(option = vote.first, name = who, userId = line.authorId)
			},
		)
	}
}