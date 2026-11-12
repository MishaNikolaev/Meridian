package com.nmichail.meridian.component.photopicker.di

import com.nmichail.meridian.component.photopicker.data.api.createPhotoApi
import com.nmichail.meridian.component.photopicker.data.repository.PhotoRepositoryImpl
import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import com.nmichail.meridian.core.di.PerFeature
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
object PhotoModule {
	@Provides
	@PerFeature
	@JvmStatic
	fun providePhotoRepository(ktorfit: Ktorfit): PhotoRepository = PhotoRepositoryImpl(
		api = ktorfit.createPhotoApi(),
	)
}