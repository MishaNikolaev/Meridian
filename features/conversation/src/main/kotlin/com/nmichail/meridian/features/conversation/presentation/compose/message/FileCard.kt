package com.nmichail.meridian.features.conversation.presentation.compose.message

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoRequest
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.shared.chat.data.mapper.mediaType
import com.nmichail.meridian.shared.chat.data.mapper.storedBytes
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

@Composable
internal fun FileCard(line: ChatLine, onOpen: () -> Unit) {
	val kind = line.fileType.mediaType()
	val context = LocalContext.current

	Row(
		modifier = Modifier.clickable(onClick = onOpen),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (kind.startsWith("image/") && line.fileId.isNotEmpty()) {
			AsyncImage(
				model = photoRequest(context, line.fileId, pixels = 240),
				contentDescription = line.fileName,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(72.dp)
					.clip(RoundedCornerShape(10.dp)),
			)
		}
		Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
			Text(
				text = line.fileName,
				style = MeridianType.bodyDefault,
				color = palette.accentPrimary,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = line.fileType.fileNote(),
				style = MeridianType.chatMeta,
				color = palette.textSecondary,
				maxLines = 1,
			)
		}
	}
}

@Suppress("MagicNumber")
private fun String.fileNote(): String {
	val kind = mediaType().substringAfter('/', "").uppercase().let { raw ->
		if (raw == "JPEG") "JPG" else raw.substringBefore('+')
	}
	val size = storedBytes().byteLabel()
	return if (size.isEmpty()) kind else "$size $kind"
}

@Suppress("MagicNumber")
private fun Long.byteLabel(): String {
	val kilo = 1024.0
	if (this <= 0L) {
		return ""
	}
	if (this < kilo) {
		return "$this Б"
	}
	val kb = this / kilo
	if (kb < kilo) {
		return "${"%.1f".format(kb).replace('.', ',')} КБ"
	}
	return "${"%.1f".format(kb / kilo).replace('.', ',')} МБ"
}
