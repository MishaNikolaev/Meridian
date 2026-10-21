package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.features.conversation.R

private const val SEND = 40
private const val FIELD_CORNER = 20
private const val ATTACH = 24

@Composable
internal fun Composer(draft: String, onDraft: (String) -> Unit, onSend: () -> Unit, modifier: Modifier = Modifier) {
	val palette = MeridianThemeTokens.palette
	Row(
		modifier = modifier
			.fillMaxWidth()
			.background(palette.bar)
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(palette.borderDefault, Offset.Zero, Offset(size.width, 0f), stroke)
			}
			.padding(horizontal = 14.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Image(painter = painterResource(R.drawable.ic_attach), contentDescription = null, modifier = Modifier.size(ATTACH.dp))
		Row(
			modifier = Modifier
				.weight(1f)
				.clip(RoundedCornerShape(FIELD_CORNER.dp))
				.background(palette.field)
				.padding(start = 14.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			BasicTextField(
				value = draft,
				onValueChange = onDraft,
				textStyle = MeridianType.bodyRegular.copy(color = palette.textPrimary),
				cursorBrush = SolidColor(palette.accentPrimary),
				modifier = Modifier.weight(1f),
				decorationBox = { inner ->
					Box {
						if (draft.isEmpty()) {
							Text(text = stringResource(R.string.conversation_message), style = MeridianType.bodyRegular, color = palette.textSecondary)
						}
						inner()
					}
				},
			)
			Image(painter = painterResource(R.drawable.ic_smile), contentDescription = null)
		}
		Box(
			modifier = Modifier
				.size(SEND.dp)
				.clip(CircleShape)
				.background(palette.accentButton)
				.clickable(onClick = onSend),
			contentAlignment = Alignment.Center,
		) {
			Image(painter = painterResource(R.drawable.ic_send), contentDescription = null)
		}
	}
}
