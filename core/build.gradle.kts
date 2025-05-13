plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group = "me.arthed"
version = "1.4.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "worldedit-repo"
        url = uri("https://maven.enginehub.org/repo/")
    }
    maven("https://repo.papermc.io/repository/maven-public/")
    gradlePluginPortal()
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":nms:nms_1_20_5"))
    implementation(project(":nms:nms_1_21"))
    implementation(project(":nms:nms_1_21_3"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("CustomBiomeColors-${version}-(MC-1.20.5-1.21.5).jar")
        minimize()
    }
}
