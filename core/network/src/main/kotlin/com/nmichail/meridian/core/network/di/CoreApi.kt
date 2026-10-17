package com.nmichail.meridian.core.network.di

import com.nmichail.meridian.core.network.device.DeviceIdentityStore
import com.nmichail.meridian.core.network.session.SessionEvents
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionStore
import com.nmichail.meridian.core.storage.LocalStore
import de.jensklingenberg.ktorfit.Ktorfit

interface CoreApi {

	fun ktorfit(): Ktorfit

	fun sessionStore(): SessionStore

	fun sessionRefresher(): SessionRefresher

	fun sessionEvents(): SessionEvents

	fun deviceIdentityStore(): DeviceIdentityStore

	fun localStore(): LocalStore
}
