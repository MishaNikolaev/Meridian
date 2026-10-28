package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

internal fun mergeReactionViewers(
	remote: Map<String, List<ChatReaction>>,
	current: Map<String, List<ChatReaction>>,
): Map<String, List<ChatReaction>> = (remote.keys + current.keys).associate { id ->
	id to mergeViewers(remote[id].orEmpty(), current[id].orEmpty())
}.filterValues { viewers -> viewers.isNotEmpty() }

private fun mergeViewers(remote: List<ChatReaction>, current: List<ChatReaction>): List<ChatReaction> {
	val mine = current.firstOrNull { viewer -> viewer.mine }
	val server = remote.map { viewer ->
		if (viewer.mine && viewer.sentAt == null) {
			viewer.copy(sentAt = mine?.sentAt)
		} else {
			viewer
		}
	}
	if (server.any { viewer -> viewer.mine } || mine == null) {
		return server
	}
	return server + mine
}