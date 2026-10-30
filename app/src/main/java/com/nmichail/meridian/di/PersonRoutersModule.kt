package com.nmichail.meridian.di

import com.nmichail.meridian.features.person.presentation.PersonRouter
import com.nmichail.meridian.navigation.router.person.PersonRouterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class PersonRoutersModule {

	@Binds
	abstract fun bindPersonRouter(impl: PersonRouterImpl): PersonRouter
}
