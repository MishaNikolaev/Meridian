package com.nmichail.meridian.core.network.di

import android.content.Context
import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionEvents
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.core.di.ApplicationContext
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.serialization.json.Json

interface CoreApi {

	fun ktorfit(): Ktorfit

	fun json(): Json

	fun networkConfig(): NetworkConfig

	fun sessionRepository(): SessionRepository

	fun sessionRefresher(): SessionRefresher

	fun sessionEvents(): SessionEvents

	fun deviceIdentityRepository(): DeviceIdentityRepository

	fun errorRouter(): ErrorRouter

	@ApplicationContext
	fun context(): Context
}
