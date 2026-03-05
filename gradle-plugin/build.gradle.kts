plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("composeFlowDiagram") {
            id = "com.mokelab.android.cfd"
            implementationClass = "ComposeFlowDiagramPlugin"
        }
    }
}

dependencies {
    // Android Gradle Plugin や KSP Plugin の型にアクセスするために必要
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

group = "com.mokelab.android"
version = "1.0.0"

publishing {
    repositories {
        maven {
            url = uri(rootProject.layout.projectDirectory.dir("docs/repo"))
        }
    }
}