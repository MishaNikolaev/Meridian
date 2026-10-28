package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.R
import com.nmichail.meridian.component.ui.R as UiR

@Composable
internal fun SearchEmpty(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier.padding(horizontal = 40.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		EmptyMark()
		Text(
			text = stringResource(R.string.search_empty_title),
			modifier = Modifier.padding(top = 14.dp),
			style = MeridianType.titleSection,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)
		Text(
			text = stringResource(R.string.search_empty_text),
			modifier = Modifier.padding(top = 8.dp),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
private fun EmptyMark() {
	Box(
		modifier = Modifier.size(128.dp),
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.size(128.dp)
				.background(palette.accentPrimarySoft, CircleShape),
		)
		Dot(modifier = Modifier.offset(x = 36.dp, y = (-34).dp))
		Dot(modifier = Modifier.offset(x = (-40).dp, y = 28.dp))
		Dot(modifier = Modifier.offset(x = 42.dp, y = 30.dp))
		Box(
			modifier = Modifier
				.size(72.dp)
				.shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp))
				.clip(RoundedCornerShape(22.dp))
				.background(palette.surface),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_search),
				contentDescription = null,
				modifier = Modifier.size(36.dp),
				colorFilter = ColorFilter.tint(palette.accentPrimary),
			)
		}
	}
}

@Composable
private fun Dot(modifier: Modifier) {
	Box(
		modifier = modifier
			.size(8.dp)
			.background(palette.accentPrimary, CircleShape),
	)
}
