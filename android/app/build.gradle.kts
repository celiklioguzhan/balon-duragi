plugins {
    id("com.android.application")
}

android {
    namespace = "com.balondurak.oyun"
    compileSdk = 36

    defaultConfig {
        // Play'e yüklendikten sonra bu kimlik değiştirilemez.
        applicationId = "com.balondurak.oyun"
        minSdk = 26          // Android 8 ve üstü
        targetSdk = 36
        versionCode = 1      // her yeni yüklemede 1 artır
        versionName = "0.1.0"
    }

    // İmza anahtarı depoda durmaz: derleme sırasında ortam değişkenlerinden okunur (GitHub Actions ayarlar).
    // Anahtar yoksa Android'in varsayılan hata ayıklama imzası kullanılır.
    val imzaDosya = System.getenv("IMZA_DOSYA")
    signingConfigs {
        if (imzaDosya != null) {
            create("imza") {
                storeFile = file(imzaDosya)
                storePassword = System.getenv("IMZA_SIFRE")
                keyAlias = "balon"
                keyPassword = System.getenv("IMZA_SIFRE")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (imzaDosya != null) signingConfigs.getByName("imza") else signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
