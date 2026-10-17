package com.nmichail.meridian.core.di

interface ComponentHolder<C : FeatureApi> {

	fun get(): C

	fun reset()
}
