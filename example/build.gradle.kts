import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

group = "com.github.numq"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":library"))
    implementation("org.bytedeco:javacv-platform:1.5.11")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.6.11")
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "ApplicationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "example"
            packageVersion = "1.0.0"
        }
    }
}