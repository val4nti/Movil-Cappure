plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    // Kover: mide qué porcentaje del código recorren los tests unitarios
    alias(libs.plugins.kover)
}

android {
    namespace = "cl.duoc.cappucare"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.cappucare"
        minSdk = 30
        // 36 y no 37: Android 17 bloquea la conexión a la red local (el PC con los microservicios)
        // para apps que apuntan a la versión 37, salvo que pidan un permiso especial.
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
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
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    // Retrofit: llamadas HTTP a los microservicios; Gson convierte el JSON en clases Kotlin
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    testImplementation(libs.junit)
    // Para probar corrutinas y ViewModels (runTest, advanceUntilIdle, Dispatchers.setMain)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

/*
 * Cobertura de tests con Kover.
 *   Reporte:   ./gradlew koverHtmlReportDebug   → app/build/reports/kover/htmlDebug/index.html
 *   Verificar: ./gradlew koverVerifyDebug       → falla si la cobertura baja de 80 %
 * Se mide la lógica (ViewModel, Repository, validaciones y conversiones), no las pantallas.
 */
kover {
    reports {
        filters {
            includes {
                classes(
                    "cl.duoc.cappucare.utils.Validaciones",
                    "cl.duoc.cappucare.utils.FormatoKt",
                    "cl.duoc.cappucare.data.remote.MapeosKt",
                    "cl.duoc.cappucare.data.repository.IndicadoresRepository*",
                    "cl.duoc.cappucare.viewmodel.IndicadoresViewModel*"
                )
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
