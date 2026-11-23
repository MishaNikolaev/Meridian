package com.nmichail.meridian.features.calls.presentation.compose.home

import com.nmichail.meridian.features.calls.R
import com.nmichail.meridian.features.calls.domain.entity.CallDirection
import com.nmichail.meridian.features.calls.domain.entity.Meeting

internal val meetings = listOf(
	Meeting("Ретро спринта 38", "Сегодня, 16:00 8 участников", "РС", canJoin = true),
	Meeting("Планёрка команды Android", "5 участников, 45 минут · 3 сентября, 18:00", "ПА", canJoin = false),
	Meeting("Демо релиза 4.2", "12 участников, 1 час 10 минут · 28 августа", "ДР", canJoin = false),
	Meeting("JAS-7743 Внедрение платежей", "4 участника, 32 минуты · 21 августа", "JA", canJoin = false),
	Meeting("Собеседование Крылов Олег", "3 участника, 1 час 30 минут · 3 сентября, 18:00", "СК", canJoin = false),
)

internal fun CallDirection.icon(): Int = when (this) {
	CallDirection.Incoming -> R.drawable.ic_call_in
	CallDirection.Outgoing -> R.drawable.ic_call_out
	CallDirection.Group -> R.drawable.ic_call_in
	CallDirection.Missed, CallDirection.Declined -> R.drawable.ic_call_missed
}
