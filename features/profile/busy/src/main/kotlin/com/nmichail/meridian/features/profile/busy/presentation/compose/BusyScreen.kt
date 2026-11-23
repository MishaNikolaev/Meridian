package com.nmichail.meridian.features.profile.busy.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.chip.MUiChip
import com.nmichail.meridian.component.ui.chip.model.MUiChipModel
import com.nmichail.meridian.component.ui.error.MUiErrorInPlace
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.profile.busy.R
import com.nmichail.meridian.features.profile.busy.presentation.BusyIntent
import com.nmichail.meridian.features.profile.busy.presentation.BusyState
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun BusyScreen(
	state: StateFlow<BusyState>,
	onIntent: (BusyIntent) -> Unit,
) {
	val current by state.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		onIntent(BusyIntent.Load)
	}
	when (val screen = current) {
		BusyState.Initial,
		is BusyState.Loading -> Unit

		is BusyState.Error   -> Box(
			modifier = Modifier
				.fillMaxSize()
				.background(palette.page),
		) {
			MUiErrorInPlace(
				message = stringResource(screen.message),
				retry = stringResource(UiR.string.error_retry),
				back = stringResource(UiR.string.error_back),
				onRetry = { onIntent(BusyIntent.Load) },
				onBack = { onIntent(BusyIntent.Back) },
				modifier = Modifier.align(Alignment.Center),
			)
		}

		is BusyState.Content -> BusyBody(
			day = screen.day,
			slots = screen.slots,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun BusyBody(
	day: Int,
	slots: Set<Int>,
	onIntent: (BusyIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		BusyBar(onIntent)

		Column(
			modifier = Modifier
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = planLabel(day),
				style = MeridianType.bodyDefault,
				color = palette.textPrimary,
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center,
			)

			DayNumbers(day = day, onIntent = onIntent)

			Text(
				text = stringResource(R.string.busy_hint),
				style = MeridianType.captionSmall,
				color = palette.textSecondary,
			)

			planSlotLabels.forEachIndexed { index, label ->
				MUiChip(
					model = MUiChipModel(
						label = stringResource(label),
						selected = index in slots,
					),
					onClick = { onIntent(BusyIntent.Toggle(index)) },
					modifier = Modifier.fillMaxWidth(),
					textStyle = MeridianType.bodyDefault,
					horizontalPadding = 16.dp,
					verticalPadding = 14.dp,
				)
			}
		}
	}
}

@Composable
private fun BusyBar(onIntent: (BusyIntent) -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 8.dp, end = 16.dp, top = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_chevron_right),
			contentDescription = null,
			modifier = Modifier
				.size(18.dp)
				.graphicsLayer(rotationZ = 180f)
				.clickable { onIntent(BusyIntent.Back) },
		)

		Text(
			text = stringResource(R.string.busy_title),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
			modifier = Modifier.padding(start = 4.dp),
		)
	}
}

@Composable
private fun DayNumbers(day: Int, onIntent: (BusyIntent) -> Unit) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		modifier = Modifier.fillMaxWidth(),
	) {
		repeat(times = PLAN_DAYS) { index ->
			MUiChip(
				model = MUiChipModel(
					label = planNumber(index),
					selected = index == day,
				),
				onClick = { onIntent(BusyIntent.PickDay(index)) },
				modifier = Modifier.weight(1f),
				textStyle = MeridianType.captionSmall,
				horizontalPadding = 0.dp,
				verticalPadding = 8.dp,
			)
		}
	}
}