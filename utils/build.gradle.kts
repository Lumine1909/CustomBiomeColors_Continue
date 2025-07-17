plugins {
    id("java")
}

group = "io.github.lumine1909"

repositories {
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.2.2.Final")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}