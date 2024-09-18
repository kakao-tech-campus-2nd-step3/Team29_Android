plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}

android {
    namespace = "com.iguana.settings"
}
dependencies {
    implementation(projects.core.domain)
}