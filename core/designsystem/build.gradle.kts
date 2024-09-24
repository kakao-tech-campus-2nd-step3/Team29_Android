plugins {
    id("iguana.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iguana.designsystem"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}