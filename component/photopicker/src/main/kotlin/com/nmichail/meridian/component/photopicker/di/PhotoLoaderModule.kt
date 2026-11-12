package com.nmichail.meridian.component.photopicker.di

import android.content.Context
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.nmichail.meridian.core.di.ApplicationContext
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
object PhotoLoaderModule {
	@Provides
	@Singleton
	@JvmStatic
	fun provideImageLoader(
		@ApplicationContext context: Context,
		ktorfit: Ktorfit,
	): ImageLoader = loader(
		context = context,
		client = ktorfit.httpClient,
	)

	@OptIn(ExperimentalCoilApi::class)
	private fun loader(context: Context, client: HttpClient): ImageLoader = ImageLoader.Builder(context)
		.components {
			add(KtorNetworkFetcherFactory(httpClient = client))
		}
		.build()
}