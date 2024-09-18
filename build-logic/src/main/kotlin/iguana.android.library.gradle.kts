import com.iguana.notai.configureCoroutineAndroid
import com.iguana.notai.configureHiltAndroid
import com.iguana.notai.configureKotest
import com.iguana.notai.configureKotlinAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureKotest()
configureCoroutineAndroid()
configureHiltAndroid()
