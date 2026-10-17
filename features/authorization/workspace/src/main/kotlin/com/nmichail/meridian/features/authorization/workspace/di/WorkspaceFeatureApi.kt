package com.nmichail.meridian.features.authorization.workspace.di

import com.nmichail.meridian.core.di.FeatureApi
import com.nmichail.meridian.features.authorization.workspace.presentation.WorkspaceFragment

interface WorkspaceFeatureApi : FeatureApi {

	fun inject(fragment: WorkspaceFragment)
}
