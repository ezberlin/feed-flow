import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.native.coroutines)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm {
        jvmToolchain(17)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true

            export(project(":i18n"))
            export(project(":core"))
            export(libs.moko.resources)
            export(libs.touchlab.kermit.simple)

            // It should be fixed with Compose MP 1.5, but it seems not
            it.binaries.forEach { binary ->
                binary.freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                )
            }
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }

       commonMain {
            dependencies {
                implementation(project(":database"))
                implementation(libs.koin.core)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.com.prof18.rss.parser)
                implementation(libs.multiplatform.settings)

                api(project(":core"))
                api(project(":i18n"))
                api(libs.touchlab.kermit)
                api(libs.immutable.collections)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.test.junit)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.touchlab.kermit.test)
            }
        }

        val commonJvmAndroidMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.jsoup)
            }
        }

        val commonJvmAndroidTest by creating {
            dependsOn(commonTest.get())
        }

        val commonMobileMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.crashk.ios)
                implementation(libs.touchlab.kermit.crashlytics)
            }
        }

        androidMain {
            dependsOn(commonJvmAndroidMain)
            dependsOn(commonMobileMain)

            dependencies {
                implementation(libs.androidx.lifecycle.viewModel.ktx)
                implementation(libs.koin.android)
                implementation(libs.crashk.ios)
                implementation(libs.touchlab.kermit.crashlytics)
            }
        }

        val androidUnitTest by getting {
            dependsOn(commonJvmAndroidTest)

            dependencies {
                implementation(libs.junit)
                implementation(libs.org.robolectric)
                implementation(libs.androidx.test.core.ktx)
            }
        }

        iosMain {
            dependsOn(commonMobileMain)

            dependencies {
                api(libs.touchlab.kermit.simple)
            }
        }

        jvmMain {
            dependsOn(commonJvmAndroidMain)

            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
                api(libs.sentry)
            }
        }

        jvmTest {
            dependsOn(commonJvmAndroidTest)
        }
    }
}

android {
    namespace = "com.prof18.feedflow.shared"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
