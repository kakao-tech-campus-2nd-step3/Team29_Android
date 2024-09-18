plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}

android {
    namespace = "com.iguana.notetaking"
}
dependencies {
    implementation(projects.core.domain)
}