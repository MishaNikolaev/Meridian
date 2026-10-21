package com.nmichail.meridian.features.chat.presentation

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val clock = Clock.systemDefaultZone()
private val hours = DateTimeFormatter.ofPattern("HH:mm")
private val shortDay = DateTimeFormatter.ofPattern("dd.MM")

internal fun formatChatTime(iso: String?): String {
	if (iso.isNullOrBlank()) {
		return ""
	}
	val time = Instant.parse(iso).atZone(clock.zone)
	val day = time.toLocalDate()
	val today = LocalDate.now(clock)
	return when {
		day == today -> time.format(hours)
		day == today.minusDays(1) -> "Вчера"
		else -> time.format(shortDay)
	}
}
