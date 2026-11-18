package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.control.MUiSwitch
import com.nmichail.meridian.component.ui.control.model.MUiSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import kotlinx.coroutines.launch

@Composable
@Suppress("MagicNumber")
internal fun PollForm(
	onSend: (String, String, List<String>, Int, String) -> Unit,
	fraction: Float,
	onFraction: (Float) -> Unit,
	onSlide: (Float) -> Unit,
	modifier: Modifier = Modifier,
) {
	var draft by remember { mutableStateOf(startPollDraft()) }
	val onTyping = { active: Boolean ->
		if (active && fraction < 0.92f) onFraction(0.92f)
	}
	val scroll = rememberPollScroll(fraction = fraction, onSlide = onSlide)
	val filled = draft.options.count { option -> option.isNotBlank() }
	val marked = draft.correct in draft.options.indices && draft.options[draft.correct].isNotBlank()
	val ready = draft.question.isNotBlank() && filled >= 2 && (!draft.quiz || marked)
	val left = 12 - draft.options.size

	Column(modifier = modifier.nestedScroll(scroll).verticalScroll(rememberScrollState()).padding(top = 18.dp)) {
		PollTitle(ready = ready) {
			val kept = draft.options.mapIndexedNotNull { index, option ->
				option.trim().takeIf { text -> text.isNotEmpty() }?.let { text -> index to text }
			}
			val correct = if (draft.quiz) kept.indexOfFirst { item -> item.first == draft.correct } else -1
			onSend(
				draft.question.trim(),
				draft.description.trim(),
				kept.map { item -> item.second },
				correct,
				if (correct < 0) "" else draft.solution.trim(),
			)
		}
		QuestionCard(draft = draft, onDraft = { draft = it }, onTyping = onTyping)
		OptionsCard(draft = draft, onDraft = { draft = it }, onTyping = onTyping)
		if (draft.quiz && draft.correct >= 0) {
			PollLine(
				value = draft.solution,
				hint = stringResource(R.string.conversation_poll_solution),
				onValue = { draft = draft.copy(solution = it) },
				onTyping = onTyping,
				modifier = Modifier
					.padding(start = 12.dp, end = 12.dp, top = 12.dp)
					.clip(RoundedCornerShape(14.dp))
					.background(palette.surface),
			)
		}
		if (left > 0) {
			Text(
				text = stringResource(R.string.conversation_poll_more, left),
				style = MeridianType.chatMeta,
				color = palette.textSecondary,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
			)
		}
		SettingsCard(draft = draft, onDraft = { draft = it })
	}
}

@Composable
@Suppress("MagicNumber")
private fun PollTitle(ready: Boolean, onCreate: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.conversation_poll_question),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
			modifier = Modifier.weight(1f),
		)
		Text(
			text = stringResource(R.string.conversation_poll_send),
			style = MeridianType.bodyDefault,
			color = palette.textOnAccent,
			modifier = Modifier
				.clip(RoundedCornerShape(18.dp))
				.background(palette.accentPrimary.copy(alpha = if (ready) 1f else 0.4f))
				.clickable(enabled = ready, onClick = onCreate)
				.padding(horizontal = 16.dp, vertical = 6.dp),
		)
	}
}

@Composable
private fun QuestionCard(draft: PollDraft, onDraft: (PollDraft) -> Unit, onTyping: (Boolean) -> Unit) {
	Column(
		modifier = Modifier
			.padding(horizontal = 12.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(palette.surface),
	) {
		PollLine(
			value = draft.question,
			hint = stringResource(R.string.conversation_poll_ask),
			onValue = { onDraft(draft.copy(question = it)) },
			onTyping = onTyping,
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(1.dp)
				.background(palette.borderDefault),
		)
		PollLine(
			value = draft.description,
			hint = stringResource(R.string.conversation_poll_description),
			onValue = { onDraft(draft.copy(description = it)) },
			onTyping = onTyping,
		)
	}
}

@Composable
@Suppress("MagicNumber")
private fun OptionsCard(draft: PollDraft, onDraft: (PollDraft) -> Unit, onTyping: (Boolean) -> Unit) {
	val left = 12 - draft.options.size

	Column(modifier = Modifier.padding(top = 16.dp)) {
		Text(
			text = stringResource(R.string.conversation_poll_options),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		)
		Column(
			modifier = Modifier
				.padding(horizontal = 12.dp)
				.clip(RoundedCornerShape(14.dp))
				.background(palette.surface),
		) {
			draft.options.forEachIndexed { index, value ->
				OptionLine(
					value = value,
					quiz = draft.quiz,
					chosen = draft.correct == index,
					onValue = { next ->
						onDraft(draft.copy(options = draft.options.mapIndexed { item, text -> if (item == index) next else text }))
					},
					onChoose = { onDraft(draft.copy(correct = if (draft.correct == index) -1 else index)) },
					onTyping = onTyping,
				)
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(1.dp)
						.background(palette.borderDefault),
				)
			}
			Text(
				text = stringResource(R.string.conversation_poll_add),
				style = MeridianType.bodyDefault,
				color = if (left > 0) palette.accentPrimary else palette.textSecondary,
				modifier = Modifier
					.fillMaxWidth()
					.clickable(enabled = left > 0) {
						onDraft(draft.copy(options = draft.options + "", marks = draft.marks + ""))
					}
					.padding(horizontal = 14.dp, vertical = 14.dp),
			)
		}
	}
}

@Composable
private fun SettingsCard(draft: PollDraft, onDraft: (PollDraft) -> Unit) {
	val rows = pollSwitches(draft)

	Column(
		modifier = Modifier.padding(top = 8.dp, bottom = 96.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Text(
			text = stringResource(R.string.conversation_poll_settings),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		)
		Column(
			modifier = Modifier
				.padding(horizontal = 12.dp)
				.clip(RoundedCornerShape(14.dp))
				.background(palette.surface),
		) {
			rows.forEachIndexed { index, row ->
				SettingRow(row = row, onClick = { onDraft(row.flip(draft)) })
				if (index != rows.lastIndex) {
					Box(
						modifier = Modifier
							.padding(start = 36.dp)
							.fillMaxWidth()
							.height(1.dp)
							.background(palette.borderDefault),
					)
				}
			}
		}
	}
}

@Composable
@Suppress("MagicNumber")
private fun OptionLine(
	value: String,
	quiz: Boolean,
	chosen: Boolean,
	onValue: (String) -> Unit,
	onChoose: () -> Unit,
	onTyping: (Boolean) -> Unit,
) {
	val green = Color(0xFF3EBE5A)

	Row(verticalAlignment = Alignment.CenterVertically) {
		Image(
			painter = painterResource(R.drawable.ic_poll_drag),
			contentDescription = null,
			modifier = Modifier
				.padding(start = 12.dp)
				.size(size = 18.dp),
		)
		PollLine(
			value = value,
			hint = stringResource(R.string.conversation_poll_option),
			onValue = onValue,
			onTyping = onTyping,
			modifier = Modifier.weight(1f),
		)
		if (quiz) {
			Box(
				modifier = Modifier
					.padding(end = 14.dp)
					.size(size = 22.dp)
					.clip(CircleShape)
					.background(if (chosen) green else Color.Transparent)
					.border(width = 1.dp, color = if (chosen) green else palette.textSecondary, shape = CircleShape)
					.clickable(onClick = onChoose),
				contentAlignment = Alignment.Center,
			) {
				if (chosen) {
					Image(
						painter = painterResource(R.drawable.ic_poll_check),
						contentDescription = null,
						modifier = Modifier.size(size = 14.dp),
					)
				}
			}
		}
	}
}

@Composable
private fun PollLine(
	value: String,
	hint: String,
	onValue: (String) -> Unit,
	onTyping: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	val bring = remember { BringIntoViewRequester() }
	val scope = rememberCoroutineScope()

	BasicTextField(
		value = value,
		onValueChange = onValue,
		singleLine = true,
		textStyle = MeridianType.bodyDefault.copy(color = palette.textPrimary),
		cursorBrush = SolidColor(palette.accentPrimary),
		modifier = modifier
			.fillMaxWidth()
			.heightIn(min = 48.dp)
			.bringIntoViewRequester(bring)
			.onFocusChanged { state ->
				onTyping(state.isFocused)
				if (state.isFocused) {
					scope.launch {
						withFrameNanos { }
						bring.bringIntoView()
					}
				}
			}
			.padding(horizontal = 14.dp, vertical = 14.dp),
		decorationBox = { inner ->
			Box(modifier = Modifier.fillMaxWidth()) {
				if (value.isEmpty()) {
					Text(text = hint, style = MeridianType.bodyDefault, color = palette.textSecondary)
				}
				inner()
			}
		},
	)
}

@Composable
private fun SettingRow(row: PollSwitch, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 10.dp, vertical = 2.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Box(
			modifier = Modifier
				.size(20.dp)
				.clip(CircleShape)
				.background(Color(row.tint)),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(row.icon),
				contentDescription = null,
				modifier = Modifier.size(12.dp),
			)
		}
		Text(
			text = stringResource(row.title),
			style = MeridianType.bodyRegular,
			color = Color.Black,
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 8.dp),
		)
		MUiSwitch(
			model = MUiSwitchModel(checked = row.checked),
			onClick = onClick,
			modifier = Modifier.scale(scale = 0.66f),
		)
	}
}

private fun pollSwitches(draft: PollDraft) = listOf(
	PollSwitch(
		icon = R.drawable.ic_poll_eye,
		tint = 0xFF3390EC,
		title = R.string.conversation_poll_voters,
		checked = draft.voters,
	) { it.copy(voters = !it.voters) },
	PollSwitch(
		icon = R.drawable.ic_poll_multi,
		tint = 0xFFE5922A,
		title = R.string.conversation_poll_multiple,
		checked = draft.multiple,
	) { it.copy(multiple = !it.multiple) },
	PollSwitch(
		icon = R.drawable.ic_poll_plus,
		tint = 0xFF2BB5A0,
		title = R.string.conversation_poll_adding,
		checked = draft.adding,
	) { it.copy(adding = !it.adding) },
	PollSwitch(
		icon = R.drawable.ic_poll_revote,
		tint = 0xFF9B6AD6,
		title = R.string.conversation_poll_revote,
		checked = draft.revote,
	) { it.copy(revote = !it.revote) },
	PollSwitch(
		icon = R.drawable.ic_poll_shuffle,
		tint = 0xFFE07A2F,
		title = R.string.conversation_poll_shuffle,
		checked = draft.shuffle,
	) { it.copy(shuffle = !it.shuffle) },
	PollSwitch(
		icon = R.drawable.ic_poll_check,
		tint = 0xFF3EBE5A,
		title = R.string.conversation_poll_quiz,
		checked = draft.quiz,
	) { form -> form.copy(quiz = !form.quiz, multiple = if (form.quiz) form.multiple else false) },
	PollSwitch(
		icon = R.drawable.ic_poll_clock,
		tint = 0xFFE15B64,
		title = R.string.conversation_poll_timed,
		checked = draft.timed,
	) { it.copy(timed = !it.timed) },
)
