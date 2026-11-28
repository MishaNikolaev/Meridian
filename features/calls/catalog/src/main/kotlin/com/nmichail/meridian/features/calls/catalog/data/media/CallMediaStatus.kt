package com.nmichail.meridian.features.calls.catalog.data.media

data class CallMediaStatus(
	val muted: Boolean,
	val speaker: Boolean,
	val remoteVideo: Boolean,
)