# com.iguana.notetaking 패키지 전체 보존
-keep class com.iguana.notetaking.** { *; }

# PhotoView 라이브러리 보존
-keep class com.github.chrisbanes.photoview.** { *; }

# RichEditor 라이브러리 보존
-keep class jp.wasabeef.richeditor.** { *; }

# pdfbox 라이브러리 보존
-keep class org.apache.pdfbox.** { *; }
-dontwarn org.apache.pdfbox.**

# AndroidX와 Android Support 라이브러리 예외
-keep class androidx.** { *; }
-dontwarn androidx.**

# 뷰 바인딩 및 데이터 바인딩 보존
-keep class **.databinding.* { *; }
-keepclassmembers class **.databinding.* { *; }
-keepattributes *Annotation*

# Hilt 관련 예외 (Hilt는 주입 관련 코드가 Proguard에 의해 제거되지 않도록 함)
-keep class dagger.hilt.internal.** { *; }
-keep class dagger.hilt.android.internal.** { *; }
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Android 기본 클래스 유지
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

