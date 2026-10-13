package com.nmichail.meridian.di

import android.content.Context
import com.nmichail.meridian.MeridianApplication
import com.nmichail.meridian.core.navigation.di.NavigationModule
import com.nmichail.meridian.core.preferences.ApplicationContext
import com.nmichail.meridian.features.home.di.HomeDependencies
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Module(subcomponents = [ActivityComponent::class])
interface AppModule

@Singleton
@Component(
	modules = [
		AppModule::class,
		NavigationModule::class,
		RoutersModule::class,
	],
)
interface AppComponent : HomeDependencies {

	fun inject(application: MeridianApplication)

	fun activityComponent(): ActivityComponent.Factory

	@Component.Factory
	interface Factory {

		fun create(@BindsInstance @ApplicationContext context: Context): AppComponent
	}
}
