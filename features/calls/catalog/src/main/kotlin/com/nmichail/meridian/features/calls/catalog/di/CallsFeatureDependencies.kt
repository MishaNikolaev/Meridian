package com.nmichail.meridian.features.calls.catalog.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import com.nmichail.meridian.shared.calls.di.PhoneSessionApi
import com.nmichail.meridian.features.calls.signal.di.SignalSessionApi

interface CallsFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun session(): ChatSessionApi

	fun phone(): PhoneSessionApi

	fun signal(): SignalSessionApi
}

fun callsFeatureDependencies(
	core: CoreApi,
	session: ChatSessionApi,
	phone: PhoneSessionApi,
	signal: SignalSessionApi,
): CallsFeatureDependencies =
	object : DependencyHolder0<CallsFeatureDependencies>() {
		override val block: (BaseDependencyHolder<CallsFeatureDependencies>) -> CallsFeatureDependencies = { holder ->
			object : CallsFeatureDependencies {
				override fun core(): CoreApi = core

				override fun session(): ChatSessionApi = session

				override fun phone(): PhoneSessionApi = phone

				override fun signal(): SignalSessionApi = signal

				override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
			}
		}
	}.dependencies