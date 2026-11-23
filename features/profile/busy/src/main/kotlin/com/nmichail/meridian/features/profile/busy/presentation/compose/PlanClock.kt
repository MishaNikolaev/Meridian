package com.nmichail.meridian.features.profile.busy.presentation.compose

import com.nmichail.meridian.features.profile.busy.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal const val PLAN_DAYS = 7

internal val planSlotLabels = listOf(
	R.string.busy_slot_09_10,
	R.string.busy_slot_10_11,
	R.string.busy_slot_11_12,
	R.string.busy_slot_12_13,
	R.string.busy_slot_13_14,
	R.string.busy_slot_14_15,
	R.string.busy_slot_15_16,
	R.string.busy_slot_16_17,
	R.string.busy_slot_17_18,
	R.string.busy_slot_18_19,
)

private val labels = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("ru"))

internal fun planDate(index: Int): String = LocalDate.now().plusDays(index.toLong()).toString()

internal fun planLabel(index: Int): String {
	val text = LocalDate.now().plusDays(index.toLong()).format(labels)
	return text.replaceFirstChar { char -> char.titlecase(Locale.forLanguageTag("ru")) }
}

internal fun planNumber(index: Int): String = LocalDate.now().plusDays(index.toLong()).dayOfMonth.toString()