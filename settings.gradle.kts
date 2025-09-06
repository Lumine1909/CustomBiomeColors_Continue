rootProject.name = "CustomBiomeColors"
include("plugin", "core")
include("nms:nms_1_20_5")
include("nms:nms_1_21")
include("nms:nms_1_21_3")
include("nms:nms_1_21_5")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
        id("com.gradleup.shadow") version "9.0.2"
    }
}