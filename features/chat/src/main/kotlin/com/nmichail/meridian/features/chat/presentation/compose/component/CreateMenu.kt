package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.text.MUiBodyNameText
import com.nmichail.meridian.component.ui.text.MUiCaptionDefaultText
import com.nmichail.meridian.component.ui.text.MUiTitleCardText
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R

@Composable
internal fun CreateMenu(
	onGroup: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
			.background(palette.surface)
			.padding(top = 10.dp, bottom = 16.dp),
	) {
		Box(
			modifier = Modifier
				.align(Alignment.CenterHorizontally)
				.size(width = 40.dp, height = 4.dp)
				.clip(RoundedCornerShape(2.dp))
				.background(palette.borderStrong),
		)

		MUiTitleCardText(
			text = stringResource(R.string.chat_create),
			modifier = Modifier.padding(
				start = 16.dp,
				top = 10.dp,
				bottom = 6.dp
			),
		)

		CreateRow(onClick = onGroup)
	}
}

@Composable
private fun CreateRow(
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 10.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Box(
			modifier = Modifier
				.size(44.dp)
				.clip(RoundedCornerShape(13.dp))
				.background(palette.accentPrimarySoft),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_create_group),
				contentDescription = null,
				modifier = Modifier.size(22.dp),
			)
		}

		Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
			MUiBodyNameText(text = stringResource(R.string.chat_new_group))

			MUiCaptionDefaultText(
				text = stringResource(R.string.chat_new_group_hint),
				color = palette.textSecondary,
			)
		}
	}
}

@Preview
@Composable
private fun PreviewCreateMenu() {
	Screen {
		CreateMenu(onGroup = {})
	}
}