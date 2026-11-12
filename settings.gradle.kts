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
include(":component:photopicker")

include(":core:di")
include(":core:presentation")
include(":core:navigation")
include(":core:network")
include(":core:storage")
include(":core:validation")
include(":testutils")

include(":features:mainhost")
include(":features:chat")
include(":features:groups")
include(":features:search")
include(":features:conversation")
include(":features:contacts")
include(":features:calls")
include(":features:profile")
include(":features:person")
include(":features:registration")
include(":features:authorization:workspace")
include(":features:authorization:signin")
include(":features:authorization:splash")

include(":shared:session")
include(":shared:chat")

include(":app")
