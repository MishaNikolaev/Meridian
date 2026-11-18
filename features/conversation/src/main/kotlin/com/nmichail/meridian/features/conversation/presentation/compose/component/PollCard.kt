package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R

@Composable
@Suppress("MagicNumber")
internal fun PollCard(
	question: String,
	description: String,
	options: List<String>,
	counts: List<Int>,
	picked: Int,
	correct: Int,
	solution: String,
	voters: List<PollVoter>,
	onPick: (Int) -> Unit,
) {
	val total = counts.sum()
	val revealed = picked >= 0 && correct >= 0
	val openVotes = LocalPollVotes.current

	Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
		Text(text = question, style = MeridianType.chatMessage, color = palette.textPrimary)
		if (description.isNotEmpty()) {
			Text(text = description, style = MeridianType.chatMeta, color = palette.textSecondary)
		}
		options.forEachIndexed { index, option ->
			PollChoice(
				label = option,
				count = counts.getOrElse(index) { 0 },
				share = barShare(total, counts.getOrElse(index) { 0 }, revealed, index, correct),
				wash = barColor(revealed, index, correct, picked),
				onPick = { onPick(index) },
			)
		}
		if (revealed && picked != correct && solution.isNotEmpty()) {
			Text(
				text = solution,
				style = MeridianType.chatMeta,
				color = palette.textSecondary,
				modifier = Modifier.padding(horizontal = 4.dp),
			)
		}
		if (total > 0) {
			Text(
				text = stringResource(R.string.conversation_poll_votes, total),
				style = MeridianType.bodyDefault,
				color = palette.accentPrimary,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.clickable { openVotes(options, voters) }
					.padding(vertical = 6.dp),
			)
		}
	}
}

@Suppress("MagicNumber")
private fun barColor(revealed: Boolean, index: Int, correct: Int, picked: Int): Color = when {
	revealed && index == correct -> Color(color = 0xFF3EBE5A)
	revealed && index == picked -> Color(color = 0xFFE15B64)
	else -> Color(color = 0xFFD5D8DE)
}

@Suppress("MagicNumber")
private fun barShare(total: Int, count: Int, revealed: Boolean, index: Int, correct: Int): Float {
	val raw = if (total == 0) 0f else count.toFloat() / total
	return if (revealed && index == correct && raw == 0f) 0.08f else raw
}

@Composable
private fun PollChoice(label: String, count: Int, share: Float, wash: Color, onPick: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onPick)
			.padding(horizontal = 4.dp, vertical = 4.dp),
		verticalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(text = label, style = MeridianType.bodyDefault, color = palette.textPrimary)
			Text(text = count.toString(), style = MeridianType.chatMeta, color = palette.textSecondary)
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(4.dp)
				.clip(RoundedCornerShape(2.dp))
				.background(Color(color = 0xFFE6E8EE)),
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth(share)
					.fillMaxHeight()
					.background(wash),
			)
		}
	}
}
