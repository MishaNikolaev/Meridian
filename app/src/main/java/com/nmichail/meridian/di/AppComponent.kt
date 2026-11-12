package com.nmichail.meridian.di

import android.content.Context
import com.nmichail.meridian.MeridianApplication
import com.nmichail.meridian.component.photopicker.di.PhotoLoaderModule
import com.nmichail.meridian.core.navigation.di.NavigationModule
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.network.di.NetworkModule
import com.nmichail.meridian.core.di.ApplicationContext
import com.nmichail.meridian.shared.session.di.SessionDataModule
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
		PhotoLoaderModule::class,
		NavigationModule::class,
		NetworkModule::class,
		RoutersModule::class,
		SessionDataModule::class,
	],
)
interface AppComponent : CoreApi {

	fun inject(application: MeridianApplication)

	fun activityComponent(): ActivityComponent.Factory

	@Component.Factory
	interface Factory {

		fun create(@BindsInstance @ApplicationContext context: Context): AppComponent
	}
}
