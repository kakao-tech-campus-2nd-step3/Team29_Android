plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}

android {
    namespace = "com.iguana.documents"
}
dependencies {
    implementation(projects.core.domain)
}