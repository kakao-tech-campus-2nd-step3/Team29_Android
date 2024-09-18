import com.iguana.notai.configureHiltAndroid
import com.iguana.notai.configureKotestAndroid
import com.iguana.notai.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
configureKotestAndroid()