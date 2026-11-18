package com.nmichail.meridian.features.conversation.presentation

internal fun String.markedFile(bytes: Int): String = "$this+file:$bytes"

internal fun String.asFile(): Boolean = contains("+file:")

internal fun String.mediaType(): String = substringBefore("+file:")

internal fun String.storedBytes(): Long = substringAfter("+file:", "").toLongOrNull() ?: 0L
