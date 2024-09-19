plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("iguana.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.iguana.login"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation("com.kakao.sdk:v2-user:2.17.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}