pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "Meridian"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":component:ui")

include(":core:di")
include(":core:presentation")
include(":core:navigation")
include(":core:network")
include(":core:preferences")
include(":core:validation")

include(":features:home")

include(":app")
