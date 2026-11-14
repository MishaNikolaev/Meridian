package com.nmichail.meridian.di

import com.nmichail.meridian.core.network.session.SessionListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSessionListener @Inject constructor(
	private val features: FeatureComponents,
) : SessionListener {

	override fun opened() {
		features.openSession()
	}

	override fun cleared() {
		features.closeSession()
	}
}