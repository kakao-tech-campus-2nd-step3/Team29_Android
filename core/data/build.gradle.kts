plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("kotlin-kapt")
}

android {
    namespace = "com.iguana.data"
}
dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.gson)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation(projects.core.domain) // domain 에 있는 걸 구현하므로
}