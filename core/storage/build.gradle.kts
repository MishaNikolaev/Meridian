plugins {
	alias(libs.plugins.meridian.android.library)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.androidx.core.ktx)
	implementation(libs.dagger)
	implementation(libs.bouncycastle)
	kapt(libs.dagger.compiler)
}
