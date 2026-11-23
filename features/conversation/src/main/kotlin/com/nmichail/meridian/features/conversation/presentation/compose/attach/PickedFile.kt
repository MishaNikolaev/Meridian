package com.nmichail.meridian.features.conversation.presentation.compose.attach

internal data class PickedFile(
	val name: String,
	val type: String,
	val bytes: ByteArray,
	val document: Boolean = false,
)
