package com.nmichail.meridian

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil3.ImageLoader
import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.bindPhotoLoader
import com.nmichail.meridian.component.ui.theme.SelectedTheme
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
		AppCompatDelegate.setDefaultNightMode(
			when (SelectedTheme.getAppTheme()) {
				true -> AppCompatDelegate.MODE_NIGHT_YES
				false -> AppCompatDelegate.MODE_NIGHT_NO
				null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
			},
		)
		PhotoLocalDataSource.load(this)
		bindPhotoLoader(imageLoader)
		featureComponents.install(component)
		openSignedInSession()
		check(networkConfig.baseUrl.isNotBlank())
	}

	private fun openSignedInSession() {
		val session = component.sessionRepository()
		if (session.isSignedIn() || !session.getAccessToken().isNullOrBlank()) {
			featureComponents.openSession()
		}
	}
}
