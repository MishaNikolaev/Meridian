package com.nmichail.meridian

import android.app.Application
import coil3.ImageLoader
import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.bindPhotoLoader
import com.nmichail.meridian.component.ui.theme.loadTheme
import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.di.AppComponent
import com.nmichail.meridian.di.DaggerAppComponent
import com.nmichail.meridian.di.FeatureComponents
import javax.inject.Inject

class MeridianApplication : Application() {
	lateinit var component: AppComponent
		private set

	@Inject
	lateinit var networkConfig: NetworkConfig

	@Inject
	lateinit var featureComponents: FeatureComponents

	@Inject
	lateinit var imageLoader: ImageLoader

	override fun onCreate() {
		super.onCreate()

		component = DaggerAppComponent.factory().create(this)
		component.inject(this)
		loadTheme(this)
		PhotoLocalDataSource.load(this)
		bindPhotoLoader(imageLoader)
		featureComponents.install(component)
		check(networkConfig.baseUrl.isNotBlank())
	}
}
