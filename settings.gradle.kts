pluginManagement {
    includeBuild("build-logic") {
        name = "custom-build-logic"  // 포함된 빌드의 이름을 변경
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://www.jitpack.io")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(uri("https://devrepo.kakao.com/nexus/content/groups/public/"))
        maven("https://www.jitpack.io")
    }
}

rootProject.name = "notai"
include(":app")

include(":core:data")
include(":core:domain")
include(":login")
include(":feature:login")
include(":feature:dashBoard")
include(":feature:notetaking")
include(":feature:documents")
include(":feature:community")
include(":feature:ai")
include(":feature:settings")
include(":feature:userInfo")
include(":feature:favorites")
include(":core:ui")
include(":core:designsystem")
