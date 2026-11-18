package com.nmichail.meridian.component.photopicker.di

import android.content.Context
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.nmichail.meridian.core.di.ApplicationContext
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import javax.inject.Singleton
import okio.Path.Companion.toOkioPath

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
		.memoryCache {
			MemoryCache.Builder()
				.maxSizePercent(context, percent = 0.25)
				.build()
		}
		.diskCache {
			DiskCache.Builder()
				.directory(context.cacheDir.resolve("photos").toOkioPath())
				.maxSizeBytes(size = 50L * 1024 * 1024)
				.build()
		}
		.components {
			add(KtorNetworkFetcherFactory(httpClient = client))
		}
		.build()
}