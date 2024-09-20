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
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation("com.kakao.sdk:v2-user:2.20.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(project(":core:data"))

    kapt("com.google.dagger:hilt-compiler:2.44")
}