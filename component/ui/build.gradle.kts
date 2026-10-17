plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.core.validation)
	debugImplementation(libs.androidx.ui.tooling)
}
