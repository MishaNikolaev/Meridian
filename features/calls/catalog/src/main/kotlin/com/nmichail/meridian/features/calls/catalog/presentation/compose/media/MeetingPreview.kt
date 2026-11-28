package com.nmichail.meridian.features.calls.catalog.presentation.compose.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

class MeetingPreview {
	internal var capture by mutableStateOf<CallCapture?>(null)
}

internal val LocalMeetingPreview = staticCompositionLocalOf<MeetingPreview?> { null }

@Composable
fun ProvideMeetingPreview(preview: MeetingPreview, content: @Composable () -> Unit) {
	CompositionLocalProvider(LocalMeetingPreview provides preview, content = content)
}
