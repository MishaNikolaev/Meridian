plugins {
	alias(libs.plugins.meridian.kotlin.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)

	testImplementation(libs.bundles.test.junit5)
}
