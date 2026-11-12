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

val kotlinVersion = libs.findVersion("kotlin").get().requiredVersion
configurations.configureEach {
	resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

dependencies {
	implementation(libs.findLibrary("kotlin-stdlib").get())
	add("testRuntimeOnly", libs.findLibrary("test-junit-platform").get())
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
	useJUnitPlatform()
}
