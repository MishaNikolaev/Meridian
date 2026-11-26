package com.nmichail.meridian.features.calls.catalog.presentation

import com.nmichail.meridian.features.calls.catalog.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal const val PLAN_DAYS = 7

internal val planSlotLabels = listOf(
	R.string.calls_slot_09_10,
	R.string.calls_slot_10_11,
	R.string.calls_slot_11_12,
	R.string.calls_slot_12_13,
	R.string.calls_slot_13_14,
	R.string.calls_slot_14_15,
	R.string.calls_slot_15_16,
	R.string.calls_slot_16_17,
	R.string.calls_slot_17_18,
	R.string.calls_slot_18_19,
)

private val labels = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.forLanguageTag("ru"))

internal fun planDate(index: Int): String = LocalDate.now().plusDays(index.toLong()).toString()

internal fun planLabel(index: Int): String {
	val text = LocalDate.now().plusDays(index.toLong()).format(labels)
	return text.replaceFirstChar { char -> char.titlecase(Locale.forLanguageTag("ru")) }
}

internal fun planNumber(index: Int): String = LocalDate.now().plusDays(index.toLong()).dayOfMonth.toString()
