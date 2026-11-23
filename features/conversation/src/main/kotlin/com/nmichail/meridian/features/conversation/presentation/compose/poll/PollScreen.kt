package com.nmichail.meridian.features.conversation.presentation.compose.poll

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.control.MUiSwitch
import com.nmichail.meridian.component.ui.control.model.MUiSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.ConversationIntent

@Composable
internal fun OpenPoll(
	open: Boolean,
	onClose: () -> Unit,
	onIntent: (ConversationIntent) -> Unit,
) {
	if (!open) {
		return
	}

	PollScreen(
		onClose = onClose,
		onSend = { question, options, anonymous ->
			onIntent(
				ConversationIntent.SendPoll(
					question = question,
					description = "",
					options = options,
					correct = -1,
					solution = "",
					anonymous = anonymous,
				),
			)
			onClose()
		},
	)
}

@Composable
internal fun PollScreen(
	onClose: () -> Unit,
	onSend: (String, List<String>, Boolean) -> Unit,
) {
	var question by remember { mutableStateOf("") }
	var options by remember { mutableStateOf(listOf("")) }
	var anonymous by remember { mutableStateOf(false) }
	var multiple by remember { mutableStateOf(true) }
	val ready = question.isNotBlank() && options.count { option -> option.isNotBlank() } >= 2

	BackHandler(onBack = onClose)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.canvas)
			.imePadding(),
	) {
		PollBar(onClose = onClose)
		Column(
			modifier = Modifier
				.weight(1f)
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp)
				.padding(top = 14.dp, bottom = 12.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			PollQuestion(question = question, onQuestion = { question = it })
			Text(
				text = stringResource(R.string.conversation_poll_options),
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
				modifier = Modifier.padding(start = 16.dp, top = 2.dp),
			)
			options.forEachIndexed { index, option ->
				val adding = index == options.lastIndex
				PollOption(
					value = option,
					hint = if (adding) stringResource(R.string.conversation_poll_add_option) else "",
					onValue = { options = options.edited(index, it) },
					onRemove = if (adding) null else ({ options = options.without(index) }),
				)
			}

			PollSettings(
				anonymous = anonymous,
				multiple = multiple,
				onAnonymous = { anonymous = !anonymous },
				onMultiple = { multiple = !multiple },
			)
		}
		PollSend(ready = ready) {
			onSend(question.trim(), options.map { option -> option.trim() }.filter { option -> option.isNotEmpty() }, anonymous)
		}
	}
}

@Composable
private fun PollBar(onClose: () -> Unit) {
	val border = palette.borderDefault

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				drawLine(
					color = border,
					start = Offset(0f, size.height),
					end = Offset(size.width, size.height),
					strokeWidth = 1.dp.toPx(),
				)
			}
			.padding(8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = stringResource(UiR.string.error_back),
			modifier = Modifier
				.size(18.dp)
				.clickable(onClick = onClose),
		)

		Text(
			text = stringResource(R.string.conversation_poll_new),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
		)
	}
}

@Composable
private fun PollQuestion(question: String, onQuestion: (String) -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		Text(
			text = stringResource(R.string.conversation_poll_question),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)

		PollOption(
			value = question,
			hint = stringResource(R.string.conversation_poll_ask),
			onValue = onQuestion,
			onRemove = null,
		)
	}
}

@Composable
private fun PollOption(
	value: String,
	hint: String,
	onValue: (String) -> Unit,
	onRemove: (() -> Unit)?,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(14.dp))
			.background(palette.field)
			.padding(horizontal = 16.dp, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		BasicTextField(
			value = value,
			onValueChange = onValue,
			textStyle = MeridianType.bodyRegular.copy(color = palette.textPrimary),
			cursorBrush = SolidColor(palette.accentPrimary),
			modifier = Modifier.weight(1f),
			decorationBox = { inner ->
				Box {
					if (value.isEmpty()) {
						Text(text = hint, style = MeridianType.bodyRegular, color = palette.textTertiary)
					}
					inner()
				}
			},
		)

		if (onRemove != null) {
			Image(
				painter = painterResource(R.drawable.ic_poll_close),
				contentDescription = null,
				modifier = Modifier
					.size(18.dp)
					.clickable(onClick = onRemove),
			)
		}
	}
}

@Composable
private fun PollSettings(
	anonymous: Boolean,
	multiple: Boolean,
	onAnonymous: () -> Unit,
	onMultiple: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface),
	) {
		PollSetting(
			title = stringResource(R.string.conversation_poll_anonymous),
			checked = anonymous,
			onClick = onAnonymous,
		)

		PollSetting(
			title = stringResource(R.string.conversation_poll_multiple),
			checked = multiple,
			onClick = onMultiple,
		)
	}
}

@Composable
private fun PollSetting(title: String, checked: Boolean, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				start = 16.dp,
				end = 14.dp,
				top = 14.dp,
				bottom = 14.dp
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_poll_mark),
			contentDescription = null,
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = title,
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		MUiSwitch(model = MUiSwitchModel(checked = checked), onClick = onClick)
	}
}

@Composable
@Suppress("MagicNumber")
private fun PollSend(ready: Boolean, onSend: () -> Unit) {
	Text(
		text = stringResource(R.string.conversation_poll_submit),
		style = MeridianType.bodyName,
		color = palette.textOnAccent,
		modifier = Modifier
			.fillMaxWidth()
			.windowInsetsPadding(WindowInsets.navigationBars)
			.padding(16.dp)
			.clip(RoundedCornerShape(16.dp))
			.background(palette.accentButton.copy(alpha = if (ready) 1f else 0.4f))
			.clickable(enabled = ready, onClick = onSend)
			.padding(vertical = 16.dp),
		textAlign = TextAlign.Center,
	)
}

@Suppress("MagicNumber")
private fun List<String>.edited(index: Int, text: String): List<String> {
	val next = mapIndexed { item, value -> if (item == index) text else value }
	return if (index == lastIndex && text.isNotEmpty() && size < 12) next + "" else next
}

private fun List<String>.without(index: Int): List<String> {
	val next = filterIndexed { item, _ -> item != index }
	return if (next.isEmpty() || next.last().isNotEmpty()) next + "" else next
}