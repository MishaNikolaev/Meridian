plugins {
	id("com.android.application") apply false
	id("com.android.library") apply false
	id("org.jetbrains.kotlin.android") apply false
	id("org.jetbrains.kotlin.jvm") apply false
	id("org.jetbrains.kotlin.kapt") apply false
	id("org.jetbrains.kotlin.plugin.compose") apply false
	id("io.gitlab.arturbosch.detekt") apply false

	alias(libs.plugins.meridian.android.library) apply false
	alias(libs.plugins.meridian.kotlin.library) apply false
	alias(libs.plugins.meridian.detekt) apply false
}

allprojects {
	apply(plugin = "base")
}
