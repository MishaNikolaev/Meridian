package com.nmichail.meridian.shared.calls.data.mapper

import com.nmichail.meridian.shared.calls.domain.entity.CallMedia
import com.nmichail.meridian.shared.calls.domain.entity.CallPhase

fun String.toCallPhase(): CallPhase? = when (this) {
	"RINGING" -> CallPhase.RINGING
	"ACCEPTED" -> CallPhase.ACCEPTED
	"DECLINED" -> CallPhase.DECLINED
	"ENDED" -> CallPhase.ENDED
	"MISSED" -> CallPhase.MISSED
	else -> null
}

fun CallPhase.toWire(): String = when (this) {
	CallPhase.RINGING -> "RINGING"
	CallPhase.ACCEPTED -> "ACCEPTED"
	CallPhase.DECLINED -> "DECLINED"
	CallPhase.ENDED -> "ENDED"
	CallPhase.MISSED -> "MISSED"
}

fun String.toCallMedia(): CallMedia? = when (this) {
	"AUDIO" -> CallMedia.AUDIO
	"VIDEO" -> CallMedia.VIDEO
	else -> null
}

fun CallMedia.toWire(): String = when (this) {
	CallMedia.AUDIO -> "AUDIO"
	CallMedia.VIDEO -> "VIDEO"
}
