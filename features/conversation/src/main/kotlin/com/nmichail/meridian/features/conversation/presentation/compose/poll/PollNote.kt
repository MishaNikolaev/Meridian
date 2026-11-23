package com.nmichail.meridian.features.conversation.presentation.compose.poll

import androidx.compose.runtime.staticCompositionLocalOf
import com.nmichail.meridian.features.conversation.presentation.ConversationState
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatNote
import com.nmichail.meridian.shared.chat.domain.usecase.SendNoteUseCase
import kotlinx.coroutines.CoroutineScope

internal val LocalTallies = staticCompositionLocalOf { emptyMap<String, PollVotes>() }

internal val LocalPollVotes = staticCompositionLocalOf<(List<String>, List<PollVoter>) -> Unit> { { _, _ -> } }

internal val LocalComposePoll = staticCompositionLocalOf { {} }

internal fun note(
	current: ConversationState.Content?,
	note: ChatNote,
	send: SendNoteUseCase,
	read: () -> ConversationState,
	publish: (ConversationState) -> Unit,
	reload: (String) -> Unit,
	run: (suspend CoroutineScope.() -> Unit) -> Unit,
) {
	if (current == null || current.sending || current.bot) {
		return
	}

	publish(current.copy(sending = true))

	run {
		send(current.chatId, current.rootId.orEmpty(), note)

		val shown = read() as? ConversationState.Content ?: return@run

		publish(shown.copy(sending = false))
		reload(current.chatId)
	}
}

internal fun List<ChatLine>.tallies(): Map<String, PollVotes> {
	val grouped = mapNotNull { line ->
		val vote = line.vote ?: return@mapNotNull null

		vote.pollId to (vote.option to line)
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