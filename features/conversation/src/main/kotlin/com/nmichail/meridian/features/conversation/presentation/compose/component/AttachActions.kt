package com.nmichail.meridian.features.conversation.presentation.compose.component

import android.content.Context
import android.net.Uri

internal class AttachActions(
	val ask: () -> Unit,
	val reload: () -> Unit,
	val camera: () -> Unit,
	val browse: () -> Unit,
	val images: () -> Unit,
	val open: (Uri) -> Unit,
	val send: (List<Uri>, String) -> Unit,
	val allowed: (Context) -> Boolean,
)