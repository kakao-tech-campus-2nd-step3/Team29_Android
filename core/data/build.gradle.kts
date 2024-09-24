plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}

android {
    namespace = "com.iguana.data"
}
dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.gson)
    implementation(projects.core.domain) // domain 에 있는 걸 구현하므로
}