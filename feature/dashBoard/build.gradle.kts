plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("iguana.android.feature")
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.iguana.dashBoard"

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}
dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.designsystem)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(projects.core.data)
    implementation(libs.pdfbox)
    implementation(libs.androidx.fragment.ktx)
    implementation(projects.feature.notetaking)

    // Test dependencies
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.kotlin.test)
}