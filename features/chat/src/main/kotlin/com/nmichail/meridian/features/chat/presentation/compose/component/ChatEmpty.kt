package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R

@Composable
internal fun ChatEmpty(
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		EmptyMark()

		Text(
			text = stringResource(R.string.chat_empty),
			modifier = Modifier.padding(top = 14.dp),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)

		Text(
			text = stringResource(R.string.chat_empty_text),
			modifier = Modifier.padding(top = 14.dp),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
private fun EmptyMark() {
	Box(modifier = Modifier.size(128.dp)) {
		Box(
			modifier = Modifier
				.size(128.dp)
				.background(palette.accentPrimarySoft, CircleShape),
		)
		MarkCircle(
			size = 10.dp,
			modifier = Modifier.offset(x = 16.dp, y = 34.dp),
		)

		MarkCircle(
			size = 7.dp,
			modifier = Modifier.offset(x = 114.dp, y = 22.dp),
		)

		MarkCircle(
			size = 12.dp,
			modifier = Modifier.offset(x = 104.dp, y = 100.dp),
		)

		Box(
			modifier = Modifier
				.offset(x = 6.dp, y = 92.dp)
				.size(16.dp)
				.border(
					width = 2.dp,
					color = palette.accentPrimary,
					shape = CircleShape
				),
		)

		Box(
			modifier = Modifier
				.offset(x = 28.dp, y = 26.dp)
				.size(72.dp)
				.shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp))
				.clip(RoundedCornerShape(22.dp))
				.background(palette.page)
				.border(
					width = 1.dp,
					color = palette.borderDefault,
					shape = RoundedCornerShape(22.dp)
				),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_empty_chat),
				contentDescription = null,
				modifier = Modifier.size(36.dp),
			)
		}
	}
}

@Composable
private fun MarkCircle(size: Dp, modifier: Modifier) {
	Box(
		modifier = modifier
			.size(size)
			.background(palette.accentPrimary, CircleShape),
	)
}

@Preview
@Composable
private fun PreviewChatEmpty() {
	Screen {
		ChatEmpty()
	}
}