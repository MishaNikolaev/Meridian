package com.nmichail.meridian.features.chat.presentation.compose.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.button.MUiFab
import com.nmichail.meridian.component.ui.button.MUiIconButton
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.tab.MUiHeader
import com.nmichail.meridian.component.ui.tab.MUiTab
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.chat.R
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatSection
import com.nmichail.meridian.component.ui.R as UiR

@Composable
internal fun ServerUnavailable(
	onIntent: (ChatIntent) -> Unit,
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		Column(modifier = Modifier.fillMaxSize()) {
			UnavailableBar(onIntent = onIntent)

			UnavailableTabs(onIntent = onIntent)

			UnavailableBody(
				onIntent = onIntent,
				modifier = Modifier.weight(1f),
			)
		}

		MUiFab(onClick = { onIntent(ChatIntent.ChangeCreate(open = true)) })
	}
}

@Composable
private fun UnavailableBar(onIntent: (ChatIntent) -> Unit) {
	MUiHeader(
		model = MUiTitleSwitchModel(
			leadingTitle = stringResource(R.string.chat_corporate),
			leadingCount = 0,
			trailingTitle = stringResource(R.string.chat_external),
			trailingCount = 0,
			leadingSelected = true,
		),
		onLeadingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = true)) },
		onTrailingClick = { onIntent(ChatIntent.ChangeCorporate(corporate = false)) },
	) {
		MUiIconButton(
			icon = UiR.drawable.ic_search,
			onClick = { onIntent(ChatIntent.OpenSearch) },
		)

		MUiIconButton(
			icon = R.drawable.ic_bell,
			onClick = null,
		)
	}
}

@Composable
private fun UnavailableTabs(onIntent: (ChatIntent) -> Unit) {
	val border = palette.borderDefault
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(
					border,
					Offset(0f, size.height),
					Offset(size.width, size.height),
					stroke,
				)
			}
			.padding(horizontal = 6.dp),
	) {
		ChatSection.entries.forEach { section ->
			MUiTab(
				model = MUiTabModel(
					label = stringResource(section.sectionLabel()),
					selected = section == ChatSection.ALL,
				),
				onClick = { onIntent(ChatIntent.ChangeSection(section = section)) },
			)
		}
	}
}

@Composable
private fun UnavailableBody(
	onIntent: (ChatIntent) -> Unit,
	modifier: Modifier = Modifier,
) {
	val target = stringResource(R.string.chat_server_name)
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		UnavailableMark()

		Text(
			text = stringResource(R.string.chat_server_title),
			modifier = Modifier.padding(top = 14.dp),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)

		Text(
			text = stringResource(R.string.chat_server_text, target),
			modifier = Modifier.padding(top = 14.dp),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
		)

		Text(
			text = stringResource(R.string.chat_server_retry),
			modifier = Modifier
				.padding(top = 14.dp)
				.clip(RoundedCornerShape(16.dp))
				.background(palette.accentButton)
				.clickable { onIntent(ChatIntent.Retry) }
				.padding(horizontal = 26.dp, vertical = 13.dp),
			style = MeridianType.bodyDefault,
			color = palette.textOnAccent,
		)

		Text(
			text = stringResource(R.string.chat_server_change),
			modifier = Modifier
				.padding(top = 10.dp)
				.clickable { onIntent(ChatIntent.ChangeServer) },
			style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
			color = palette.accentPrimary,
		)
	}
}

@Composable
private fun UnavailableMark() {
	Box(
		modifier = Modifier.size(128.dp),
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.size(128.dp)
				.background(palette.statusErrorSoft, CircleShape),
		)

		MarkCircle(
			size = 10.dp,
			modifier = Modifier.offset(x = (-48).dp, y = (-30).dp),
		)

		MarkCircle(
			size = 7.dp,
			modifier = Modifier.offset(x = 50.dp, y = (-42).dp),
		)

		MarkCircle(
			size = 12.dp,
			modifier = Modifier.offset(x = 40.dp, y = 36.dp),
		)

		MarkCircle(
			size = 16.dp,
			modifier = Modifier.offset(x = (-52).dp, y = 28.dp),
		)

		Box(
			modifier = Modifier
				.size(72.dp)
				.shadow(elevation = 8.dp, shape = RoundedCornerShape(22.dp))
				.clip(RoundedCornerShape(22.dp))
				.background(palette.page)
				.border(width = 1.dp, color = palette.borderDefault, shape = RoundedCornerShape(22.dp)),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_cloud_off),
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
			.background(palette.statusError, CircleShape),
	)
}

private fun ChatSection.sectionLabel(): Int = when (this) {
	ChatSection.ALL         -> R.string.chat_section_all

	ChatSection.CATALOG     -> R.string.chat_section_catalog

	ChatSection.BOTS        -> R.string.chat_section_bots

	ChatSection.DISCUSSIONS -> R.string.chat_section_discussions
}

@Preview
@Composable
private fun PreviewServerUnavailable() {
	Screen {
		ServerUnavailable(onIntent = {})
	}
}