plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.ktorfit)

	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.component.photopicker)
	implementation(projects.core.network)
	implementation(projects.shared.chat)
	implementation(projects.shared.calls)
	implementation(projects.shared.session)
}
