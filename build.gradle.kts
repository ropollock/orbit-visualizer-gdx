import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val gdxVersion = "1.10.0"
val kotlinVersion = "1.6.10"
val mainClassName = "com.triskil.orbitvisualizer.Main"

application {
    mainClass.set(mainClassName)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
}

tasks.withType<JavaExec> {
    systemProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true")
    systemProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "false")
}

tasks.named("run", JavaExec::class.java) {
    systemProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true")
    systemProperty("org.lwjgl.opengl.Display.enableOSXFullscreenModeAPI", "false")
}
