plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.30"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use JCenter & Maven for resolving dependencies.
    jcenter()
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")

    // Use the Javacord Library to create a discord bot.
    implementation("org.javacord:javacord:3.1.2")

    //Use the Lavaplayer to stream music in a high quality
    implementation("com.sedmelluq:lavaplayer:1.3.72")

    //Use the Emoji Library to encode evert emoji with UTF-8
    implementation("com.vdurmont:emoji-java:5.1.1")

    //Use the mysql connector driver to connect into the cluby database
    implementation("mysql:mysql-connector-java:8.0.23")
}

application {
    // Define the main class for the application.
    mainClass.set("com.github.russiadiktatorv2.clubybot.core.ProgramLoaderKt")
}