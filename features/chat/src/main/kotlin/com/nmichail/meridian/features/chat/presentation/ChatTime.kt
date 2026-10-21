package com.nmichail.meridian.features.chat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.features.chat.R
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val clock = Clock.systemDefaultZone()
private val hours = DateTimeFormatter.ofPattern("HH:mm")
private val shortDay = DateTimeFormatter.ofPattern("dd.MM")

@Composable
internal fun formatChatTime(iso: String?): String {
	if (iso.isNullOrBlank()) {
		return ""
	}
	val time = Instant.parse(iso).atZone(clock.zone)
	val day = time.toLocalDate()
	val today = LocalDate.now(clock)
	return when {
		day == today -> time.format(hours)
		day == today.minusDays(1) -> stringResource(R.string.chat_yesterday)
		else -> time.format(shortDay)
	}
}
