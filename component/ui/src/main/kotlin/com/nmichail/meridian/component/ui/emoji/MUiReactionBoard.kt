package com.nmichail.meridian.component.ui.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun MUiReactionBoard(
	reactions: List<MessageReaction>,
	selected: Set<String>,
	onPick: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.heightIn(max = 220.dp)
			.verticalScroll(rememberScrollState()),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		reactions.chunked(size = 6).forEach { line ->
			Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
				line.forEach { reaction ->
					val chosen = reaction.glyph in selected

					Text(
						text = reaction.glyph,
						style = MeridianType.reactionMenu,
						modifier = Modifier
							.clip(CircleShape)
							.background(
								select(
									active = chosen,
									on = palette.accentPrimarySoft,
									off = Color.Transparent,
								),
							)
							.clickable { onPick(reaction.glyph) },
					)
				}
			}
		}
	}
}