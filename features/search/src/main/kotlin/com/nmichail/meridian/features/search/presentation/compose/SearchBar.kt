package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.search.R
import com.nmichail.meridian.features.search.presentation.SearchIntent
import com.nmichail.meridian.features.search.presentation.SearchState

@Composable
internal fun SearchBar(
	state: SearchState.Content,
	onIntent: (SearchIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = null,
			modifier = Modifier
				.size(24.dp)
				.clip(RoundedCornerShape(12.dp))
				.clickable { onIntent(SearchIntent.NavigateBack) },
			colorFilter = ColorFilter.tint(palette.textPrimary),
		)
		SearchField(
			query = state.query,
			onIntent = onIntent,
			modifier = Modifier
				.padding(start = 8.dp)
				.weight(1f),
		)
	}
}

@Composable
private fun SearchField(
	query: String,
	onIntent: (SearchIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	val focus = remember { FocusRequester() }
	val keyboard = LocalSoftwareKeyboardController.current
	LaunchedEffect(Unit) {
		focus.requestFocus()
		keyboard?.show()
	}
	BasicTextField(
		value = query,
		onValueChange = { onIntent(SearchIntent.ChangeQuery(query = it)) },
		modifier = modifier.focusRequester(focus),
		textStyle = MeridianType.bodyDefault.copy(color = palette.textPrimary),
		cursorBrush = SolidColor(palette.accentPrimary),
		singleLine = true,
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
		keyboardActions = KeyboardActions(
			onSearch = { onIntent(SearchIntent.Submit) },
		),
		decorationBox = { inner ->
			FieldBox(
				query = query,
				onIntent = onIntent,
				inner = inner,
			)
		},
	)
}

@Composable
private fun FieldBox(
	query: String,
	onIntent: (SearchIntent) -> Unit,
	inner: @Composable () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(14.dp))
			.background(palette.field)
			.border(
				width = 1.5.dp,
				color = palette.accentButton,
				shape = RoundedCornerShape(14.dp),
			)
			.padding(horizontal = 12.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_search),
			contentDescription = null,
			modifier = Modifier.size(20.dp),
			colorFilter = ColorFilter.tint(palette.textSecondary),
		)
		FieldValue(
			query = query,
			inner = inner,
			modifier = Modifier
				.padding(horizontal = 8.dp)
				.weight(1f),
		)
		if (query.isNotEmpty()) {
			Image(
				painter = painterResource(UiR.drawable.ic_close),
				contentDescription = null,
				modifier = Modifier
					.size(18.dp)
					.clickable { onIntent(SearchIntent.ChangeQuery(query = "")) },
				colorFilter = ColorFilter.tint(palette.textSecondary),
			)
		}
	}
}

@Composable
private fun FieldValue(
	query: String,
	inner: @Composable () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(modifier = modifier) {
		if (query.isEmpty()) {
			Text(
				text = stringResource(R.string.search_placeholder),
				style = MeridianType.bodyDefault,
				color = palette.textTertiary,
				maxLines = 2,
			)
		}
		inner()
	}
}
