package com.nmichail.meridian.features.conversation.presentation.compose.attach

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.compose.poll.SheetHeight
import com.nmichail.meridian.features.conversation.presentation.compose.poll.rememberPollScroll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OpenStorage : ActivityResultContract<String, Uri?>() {
	override fun createIntent(context: Context, input: String): Intent {
		return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = input
			if (input == "*/*") {
				putExtra(
					DocumentsContract.EXTRA_INITIAL_URI,
					DocumentsContract.buildRootUri("com.android.externalstorage.documents", "primary"),
				)
			}
		}
	}

	override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
		if (resultCode != Activity.RESULT_OK) {
			return null
		}
		return intent?.data
	}
}

@Composable
internal fun rememberStorage(onFile: (PickedFile) -> Unit): Pair<() -> Unit, () -> Unit> {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val picked = rememberLauncherForActivityResult(OpenStorage()) { uri ->
		if (uri == null) {
			return@rememberLauncherForActivityResult
		}
		scope.launch {
			val file = withContext(Dispatchers.IO) { context.readPickedFile(uri) } ?: return@launch
			onFile(file.copy(document = true))
		}
	}
	return { picked.launch("*/*") } to { picked.launch("image/*") }
}

@Composable
internal fun FilePlaces(
	docs: List<RecentFile>,
	onStorage: () -> Unit,
	onImages: () -> Unit,
	onFile: (Uri) -> Unit,
	onAllow: () -> Unit,
	sheet: SheetHeight,
	modifier: Modifier = Modifier,
) {
	val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 88.dp
	val scroll = rememberPollScroll(fraction = sheet.fraction, onSlide = sheet.slide)

	LazyColumn(
		modifier = modifier
			.fillMaxWidth()
			.nestedScroll(scroll),
		contentPadding = PaddingValues(bottom = bottom),
	) {
		item {
			PlaceRow(
				icon = R.drawable.ic_file,
				tint = Color(color = 0xFF43A047),
				title = stringResource(R.string.conversation_storage),
				note = stringResource(R.string.conversation_storage_note),
				onClick = onStorage,
			)
		}
		item {
			PlaceRow(
				icon = R.drawable.ic_gallery,
				tint = Color(color = 0xFFF5A623),
				title = stringResource(R.string.conversation_attach_gallery),
				note = stringResource(R.string.conversation_storage_gallery),
				onClick = onImages,
			)
		}
		item {
			Text(
				text = stringResource(R.string.conversation_recent_files),
				style = MeridianType.bodyDefault,
				color = palette.accentPrimary,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
			)
		}
		if (docs.isEmpty()) {
			item {
				Text(
					text = stringResource(R.string.conversation_attach_allow),
					style = MeridianType.bodyDefault,
					color = palette.accentPrimary,
					modifier = Modifier
						.fillMaxWidth()
						.clickable(onClick = onAllow)
						.padding(horizontal = 16.dp, vertical = 12.dp),
				)
			}
		}
		items(items = docs, key = { file -> file.uri.toString() }) { file ->
			RecentRow(file = file, onClick = { onFile(file.uri) })
		}
	}
}

@Composable
private fun PlaceRow(icon: Int, tint: Color, title: String, note: String, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp),
	) {
		BoxMark(icon = icon, tint = tint)
		Column(modifier = Modifier.weight(1f)) {
			Text(text = title, style = MeridianType.bodyDefault, color = palette.textPrimary, maxLines = 1)
			Text(text = note, style = MeridianType.chatMeta, color = palette.textSecondary, maxLines = 1)
		}
	}
}

@Composable
private fun RecentRow(file: RecentFile, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp),
	) {
		if (file.image) {
			AsyncImage(
				model = file.uri,
				contentDescription = file.name,
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(44.dp)
					.clip(RoundedCornerShape(8.dp)),
			)
		} else {
			BoxMark(icon = R.drawable.ic_file, tint = Color(color = 0xFF43A047))
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = file.name,
				style = MeridianType.bodyDefault,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(text = file.bytes.bytesLabel(), style = MeridianType.chatMeta, color = palette.textSecondary)
		}
	}
}

@Composable
private fun BoxMark(icon: Int, tint: Color) {
	Image(
		painter = painterResource(icon),
		contentDescription = null,
		colorFilter = ColorFilter.tint(Color.White),
		modifier = Modifier
			.size(44.dp)
			.clip(CircleShape)
			.background(tint)
			.padding(12.dp),
	)
}

@Suppress("MagicNumber")
private fun Long.bytesLabel(): String {
	val kilo = 1024.0
	if (this < kilo) {
		return "$this Б"
	}
	val kb = this / kilo
	if (kb < kilo) {
		return "${"%.1f".format(kb)} КБ"
	}
	return "${"%.1f".format(kb / kilo)} МБ"
}