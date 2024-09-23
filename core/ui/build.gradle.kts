plugins {
    id("iguana.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iguana.ui"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(projects.core.designsystem)
    // Test dependencies
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.kotlin.test)
}