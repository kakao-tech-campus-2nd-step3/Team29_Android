plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("iguana.android.feature")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.iguana.userinfo"

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}
dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.kakao.sdk:v2-user:2.20.6")

    // Test dependencies
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.kotlin.test)
}