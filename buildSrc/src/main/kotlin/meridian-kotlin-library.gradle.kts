import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("java-library")
	id("org.jetbrains.kotlin.jvm")
	id("meridian-detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

java {
	val jvmVersion = libs.findVersion("jvmTarget").get().requiredVersion
	sourceCompatibility = JavaVersion.toVersion(jvmVersion)
	targetCompatibility = JavaVersion.toVersion(jvmVersion)
}

kotlin {
	compilerOptions {
		val jvmVersion = libs.findVersion("jvmTarget").get().requiredVersion
		jvmTarget.set(JvmTarget.fromTarget(jvmVersion))
	}
}

dependencies {
	implementation(libs.findLibrary("kotlin-stdlib").get())
}
