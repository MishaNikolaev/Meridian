package com.nmichail.meridian.di

import com.nmichail.meridian.features.calls.ring.RingRouter
import com.nmichail.meridian.features.calls.ring.RingSession
import com.nmichail.meridian.navigation.router.ring.RingRouterImpl
import com.nmichail.meridian.ui.call.CallSessionGateway
import dagger.Binds
import dagger.Module

@Module
abstract class RingRoutersModule {

	@Binds
	abstract fun bindRingRouter(impl: RingRouterImpl): RingRouter

	@Binds
	abstract fun bindRingSession(impl: CallSessionGateway): RingSession
}
