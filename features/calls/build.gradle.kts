plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.component.ui)
}
