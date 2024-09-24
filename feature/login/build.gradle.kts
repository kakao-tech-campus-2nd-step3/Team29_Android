plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("iguana.android.feature")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.iguana.login"

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation("com.kakao.sdk:v2-user:2.20.6")
    implementation(project(":core:ui"))

    // Test dependencies
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.kotlin.test)
}