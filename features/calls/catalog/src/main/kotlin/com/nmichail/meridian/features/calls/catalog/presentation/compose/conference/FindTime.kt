package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.presentation.PLAN_DAYS
import com.nmichail.meridian.features.calls.catalog.presentation.planLabel
import com.nmichail.meridian.features.calls.catalog.presentation.planNumber
import com.nmichail.meridian.features.calls.catalog.presentation.planSlotLabels
import com.nmichail.meridian.shared.busy.domain.entity.BusyPerson

private fun openSlots(people: List<BusyPerson>): List<Int> =
	planSlotLabels.filterIndexed { index, _ ->
		people.none { person -> index in person.slots }
	}

private val hours = listOf("9", "10", "11", "12", "13", "14", "15", "16", "17", "18")
private val busy = Color(color = 0xFF2563EB)
private val grid = Color(color = 0xFFEEF1F6)

@Composable
internal fun FindTime(
	day: Int,
	people: List<BusyPerson>?,
	onDay: (Int) -> Unit,
	onBack: () -> Unit,
) {
	var window by remember { mutableIntStateOf(0) }
	val free = people?.let(::openSlots).orEmpty()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		FormBar(stringResource(R.string.calls_find), onBack)
		Column(
			modifier = Modifier
				.weight(1f)
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			DayBar(day, onDay)
			if (people != null) {
				BusyTable(people)
				if (free.isNotEmpty()) {
					ChipRow(free, window.coerceAtMost(free.lastIndex)) { window = it }
				}
			}
		}
		val picked = free.getOrNull(window.coerceAtMost(free.lastIndex))
		FormButton(
			label = if (picked == null) {
				stringResource(R.string.calls_none)
			} else {
				stringResource(R.string.calls_pick, stringResource(picked))
			},
			onClick = onBack,
			inset = false,
		)
	}
}

@Composable
private fun DayBar(day: Int, onDay: (Int) -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(18.dp))
			.background(palette.surface)
			.padding(horizontal = 12.dp, vertical = 10.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Text(
			text = planLabel(day),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			modifier = Modifier.fillMaxWidth(),
			textAlign = androidx.compose.ui.text.style.TextAlign.Center,
		)
		Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
			repeat(times = PLAN_DAYS) { index ->
				val on = index == day
				Text(
					text = planNumber(index),
					style = MeridianType.captionSmall,
					color = if (on) palette.textOnAccent else palette.textPrimary,
					textAlign = androidx.compose.ui.text.style.TextAlign.Center,
					modifier = Modifier
						.weight(1f)
						.clip(RoundedCornerShape(10.dp))
						.background(if (on) palette.textPrimary else palette.page)
						.clickable { onDay(index) }
						.padding(vertical = 8.dp),
				)
			}
		}
	}
}

@Composable
private fun ChipRow(labels: List<Int>, selected: Int, onSelect: (Int) -> Unit) {
	Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
		labels.forEachIndexed { index, label ->
			val on = index == selected
			Text(
				text = stringResource(label),
				style = MeridianType.captionSmall,
				color = if (on) palette.textOnAccent else palette.textPrimary,
				modifier = Modifier
					.clip(RoundedCornerShape(12.dp))
					.background(if (on) palette.textPrimary else palette.surface)
					.clickable { onSelect(index) }
					.padding(horizontal = 10.dp, vertical = 6.dp),
			)
		}
	}
}

@Composable
private fun BusyTable(people: List<BusyPerson>) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.padding(vertical = 8.dp),
	) {
		Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 6.dp)) {
			Box(modifier = Modifier.width(92.dp))
			hours.forEach { hour ->
				Text(
					text = hour,
					style = MeridianType.captionSmall,
					color = palette.textSecondary,
					modifier = Modifier.weight(1f),
					textAlign = androidx.compose.ui.text.style.TextAlign.Center,
				)
			}
		}
		people.forEach { person -> BusyLine(person.name, person.slots) }
	}
}

@Composable
private fun BusyLine(name: String, slots: Set<Int>) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.height(36.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Box(
			modifier = Modifier
				.padding(start = 10.dp)
				.size(22.dp)
				.clip(CircleShape)
				.background(palette.page),
			contentAlignment = Alignment.Center,
		) {
			Text(text = name.take(1), style = MeridianType.captionSmall, color = palette.textPrimary)
		}
		Text(
			text = name,
			style = MeridianType.captionSmall,
			color = palette.textPrimary,
			maxLines = 1,
			modifier = Modifier
				.padding(start = 6.dp)
				.width(54.dp),
		)
		hours.forEachIndexed { index, _ ->
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxSize(),
				contentAlignment = Alignment.Center,
			) {
				Box(
					modifier = Modifier
						.align(Alignment.CenterStart)
						.width(1.dp)
						.fillMaxSize()
						.background(grid),
				)
				if (index in slots) {
					Box(
						modifier = Modifier
							.size(width = 18.dp, height = 16.dp)
							.clip(RoundedCornerShape(8.dp))
							.background(busy),
					)
				}
			}
		}
	}
}
