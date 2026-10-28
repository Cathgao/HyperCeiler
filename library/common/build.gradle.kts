import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sevtinge.hyperceiler.common"
    compileSdk = 37

    defaultConfig {
        minSdk = 35

        buildConfigField("String", "APP_MODULE_ID", "\"com.sevtinge.hyperceiler\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {}
        create("beta") {}
        create("canary") {}
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    api(libs.bundles.miuix)
    api("fan.miuix:popupwidget:1.0.13.0")
    api("fan.miuix:slidingwidget:1.0.13.0")
    api("fan.miuix:smooth:1.0.13.0")
    api("fan.miuix:stretchablewidget:1.0.13.0")
    api("fan.miuix:flexible:1.0.13.0")
    api("fan.miuix:graphics:1.0.13.0")
    api("fan.miuix:haptic:1.0.13.0")
    api("fan.miuix:viewpager2:1.0.13.0")
    api("androidx.cardview:cardview:1.0.0")
    api(libs.annotation)
    api(libs.core)
    api(libs.fragment)
    api(libs.recyclerview)
    // libxposed API 102
    compileOnlyApi(libs.libxposed.api)
}
