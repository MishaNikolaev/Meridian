plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	implementation(libs.coroutines.core)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.livedata)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.core.validation)
	implementation(libs.dagger)
	debugImplementation(libs.androidx.ui.tooling)

	testImplementation(libs.bundles.test.junit5)
}
