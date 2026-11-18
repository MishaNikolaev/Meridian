plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)
	implementation(libs.androidx.activity.compose)
	implementation(libs.coil.compose)
	implementation(libs.androidx.exifinterface)
	implementation(libs.krop.ui)
	implementation(projects.component.ui)
	implementation(projects.component.photopicker)
}
