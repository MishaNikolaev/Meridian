package com.nmichail.meridian.features.conversation.presentation.compose.component

internal data class PickedFile(
	val name: String,
	val type: String,
	val bytes: ByteArray,
	val document: Boolean = false,
)
