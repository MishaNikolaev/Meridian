package com.nmichail.meridian.features.conversation.presentation.compose.component

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val clock = Clock.systemDefaultZone()
private val hours = DateTimeFormatter.ofPattern("HH:mm")
private val day = DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))

internal fun messageClock(iso: String?): String {
	val time = zoned(iso) ?: return ""
	return time.format(hours)
}

internal fun messageDay(iso: String?): String? {
	val time = zoned(iso) ?: return null
	return time.format(day)
}

internal fun reactionToday(iso: String?): Boolean {
	val time = zoned(iso) ?: return false
	return time.toLocalDate() == LocalDate.now(clock)
}

private fun zoned(iso: String?) = iso?.takeIf { it.isNotBlank() }?.let { value ->
	Instant.parse(value).atZone(clock.zone)
}