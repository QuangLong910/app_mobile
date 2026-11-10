import org.gradle.kotlin.dsl.invoke
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

// Địa chỉ backend: mỗi người tự đặt trong local.properties (tệp này không lên kho mã),
// không ai phải sửa build.gradle.kts của người khác.
//     baseUrl=http://100.106.129.106:8080/
val diaChiBackend: String = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}.getProperty("baseUrl") ?: "http://127.0.0.1:8080/"

// Đọc khoá ký từ keystore.properties (không nằm trong kho mã). Thiếu tệp thì bản release
// vẫn build được nhưng ký bằng khoá debug, chỉ để chạy thử.
val khoaKy = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "gtvt.ktpm.thuexe"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "gtvt.ktpm.thuexe"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$diaChiBackend\"")
    }

    signingConfigs {
        if (khoaKy.isNotEmpty()) {
            create("release") {
                storeFile = rootProject.file(khoaKy.getProperty("storeFile"))
                storePassword = khoaKy.getProperty("storePassword")
                keyAlias = khoaKy.getProperty("keyAlias")
                keyPassword = khoaKy.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
            // R8 để tắt: model dữ liệu do Gson dựng bằng phản chiếu, bật lên mà thiếu keep
            // rules là tên trường bị đổi và mọi lời gọi API trả về đối tượng rỗng.
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    // OTP field
    // systemUIController(Color StatusBar)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.27.0")
    // nav controller
    implementation("androidx.navigation:navigation-compose:2.8.5")
  //
    implementation(libs.gson)
    // Tang goi API
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.compose)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.exifinterface)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
}