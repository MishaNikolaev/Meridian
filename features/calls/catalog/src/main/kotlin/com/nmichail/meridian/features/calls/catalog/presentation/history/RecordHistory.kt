package com.nmichail.meridian.features.calls.catalog.presentation.history

import com.nmichail.meridian.features.calls.catalog.domain.entity.CallDirection
import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private const val MINUTE = 60L

internal fun CallRecord.toHistory(self: String): HistoryCall = HistoryCall(
	name = title,
	time = clock(startedAt),
	direction = direction(self),
	live = phase == CallPhase.RINGING || phase == CallPhase.ACCEPTED,
	duration = duration(),
	personId = personId,
	chatId = chatId,
)

private fun CallRecord.direction(self: String): CallDirection {
	val outgoing = initiatorUserId == self
	val group = chatType == ChatType.GROUP || chatType == ChatType.CHANNEL
	return when {
		phase == CallPhase.MISSED -> CallDirection.Missed
		phase == CallPhase.DECLINED -> CallDirection.Declined
		group -> CallDirection.Group
		outgoing -> CallDirection.Outgoing
		else -> CallDirection.Incoming
	}
}

private fun CallRecord.duration(): HistorySpan? {
	val start = startedAt.instant() ?: return null
	val end = endedAt?.instant() ?: return null
	val seconds = Duration.between(start, end).seconds.coerceAtLeast(0)
	val wholeMinutes = seconds / MINUTE
	return HistorySpan(
		hours = (wholeMinutes / MINUTE).toInt(),
		minutes = (wholeMinutes % MINUTE).toInt(),
		seconds = (seconds % MINUTE).toInt(),
	)
}

private fun clock(raw: String): HistoryTime {
	val instant = raw.instant() ?: return HistoryTime.Unknown
	val zone = ZoneId.systemDefault()
	val local = instant.atZone(zone)
	val today = LocalDate.now(zone)
	return when (local.toLocalDate()) {
		today -> HistoryTime.Clock(hour = local.hour, minute = local.minute)
		today.minusDays(1) -> HistoryTime.Yesterday
		else -> HistoryTime.Day(day = local.dayOfMonth, month = local.monthValue)
	}
}

private fun String.instant(): Instant? =
	try {
		Instant.parse(this)
	} catch (_: Exception) {
		null
	}
