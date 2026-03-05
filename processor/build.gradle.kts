plugins {
    kotlin("jvm")
    id("maven-publish")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(projects.annotations)
    implementation(libs.symbol.processing.api)
    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // ArtifactId を明示（モジュール名と変えたい場合）
            groupId = "com.mokelab.android"
            artifactId = "compose-flow-diagram-processor"
            version = "1.0.0"

            // Javaコンポーネント（ソースや依存関係情報）を含める
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri(rootProject.layout.projectDirectory.dir("docs/repo"))
        }
    }
}