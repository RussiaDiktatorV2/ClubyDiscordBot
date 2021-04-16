plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.32"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use JCenter & Maven for resolving dependencies.
    jcenter()
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Javacord Library to create a discord bot.
    implementation("org.javacord:javacord:3.3.0")

    // Use the Lavaplayer to stream music in a high quality
    implementation("com.sedmelluq:lavaplayer:1.3.72")

    // Use the Emoji Library to encode evert emoji with UTF-8
    implementation("com.vdurmont:emoji-java:5.1.1")

    // Use MongoDB to save the documents in a database
    implementation("org.litote.kmongo:kmongo:4.2.5")

    //Use PostgreSQL to save the data from the discord guilds
    implementation("org.postgresql:postgresql:42.1.4")

    // Add Reflections for annotation use
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
}

application {
    // Define the main class for the application.
    mainClass.set("com.github.russiadiktatorv2.clubybot.core.ProgramLoaderKt")
}