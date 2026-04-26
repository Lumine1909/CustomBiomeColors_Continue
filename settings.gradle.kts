rootProject.name = "CustomBiomeColors"


include("core")
include("plugin")
include("nms:nms_1_20_5")
include("nms:nms_1_21")
include("nms:nms_1_21_3")
include("nms:nms_1_21_4")
include("nms:nms_1_21_5")
include("nms:nms_1_21_9")
include("nms:nms_1_21_11")
include("nms:nms_26_1")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
        id("com.gradleup.shadow") version "9.4.0"
        id("com.modrinth.minotaur") version "2.+"
    }
}