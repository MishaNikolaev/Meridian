package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R

@Composable
internal fun PlanConference(onBack: () -> Unit, onFind: () -> Unit) {
	var lobby by remember { mutableStateOf(true) }
	var record by remember { mutableStateOf(false) }
	var repeat by remember { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page)
			.verticalScroll(rememberScrollState()),
	) {
		FormBar(
			title = stringResource(R.string.calls_plan_title),
			onBack = onBack,
		)
		Column(
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			PlanSchedule(onFind)
			PlanPeople()
			PlanOptions(
				lobby = lobby,
				record = record,
				repeat = repeat,
				onLobby = { lobby = !lobby },
				onRecord = { record = !record },
				onRepeat = { repeat = !repeat },
			)
		}
		FormButton(
			label = stringResource(R.string.calls_plan),
			onClick = onBack,
		)
	}
}

@Composable
private fun PlanSchedule(onFind: () -> Unit) {
	FormCard {
		FormLine(
			icon = R.drawable.ic_call_edit,
			caption = stringResource(R.string.calls_name),
			value = "",
		)
		FormLine(
			icon = R.drawable.ic_call_calendar,
			caption = stringResource(R.string.calls_date),
			value = stringResource(R.string.calls_plan_date),
			trailing = UiR.drawable.ic_chevron_right,
			onClick = onFind,
		)
		FormLine(
			icon = R.drawable.ic_call_clock,
			caption = stringResource(R.string.calls_span),
			value = stringResource(R.string.calls_plan_span),
			trailing = UiR.drawable.ic_chevron_right,
		)
	}
}

@Composable
private fun PlanPeople() {
	Text(
		text = stringResource(R.string.calls_people),
		style = MeridianType.captionSmall,
		color = palette.textSecondary,
	)
	FormCard {
		FormLine(
			icon = R.drawable.ic_call_person_add,
			caption = stringResource(R.string.calls_people_line),
			value = stringResource(R.string.calls_people_count),
			trailing = UiR.drawable.ic_chevron_right,
		)
		FormLine(
			icon = R.drawable.ic_call_person_add,
			caption = stringResource(R.string.calls_add_people),
			value = stringResource(R.string.calls_invite_hint),
			trailing = UiR.drawable.ic_chevron_right,
		)
	}
}

@Composable
private fun PlanOptions(
	lobby: Boolean,
	record: Boolean,
	repeat: Boolean,
	onLobby: () -> Unit,
	onRecord: () -> Unit,
	onRepeat: () -> Unit,
) {
	Text(
		text = stringResource(R.string.calls_settings),
		style = MeridianType.captionSmall,
		color = palette.textSecondary,
	)
	FormCard {
		SwitchLine(
			title = stringResource(R.string.calls_lobby),
			hint = stringResource(R.string.calls_lobby_hint),
			checked = lobby,
			onClick = onLobby,
		)
		SwitchLine(
			title = stringResource(R.string.calls_record),
			hint = "",
			checked = record,
			onClick = onRecord,
		)
		SwitchLine(
			title = stringResource(R.string.calls_repeat),
			hint = "",
			checked = repeat,
			onClick = onRepeat,
		)
	}
}