plugins { kotlin("jvm") version "2.0.0" }

layout.buildDirectory = file(".build")

repositories {
  mavenLocal()
  mavenCentral()
  // google()
}

apply {
  plugin("java")
  plugin("org.jetbrains.kotlin.jvm")
}

val grpcVersion = "1.46.0"
val coroutinesVersion = "1.6.2"

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("test"))

  runtimeOnly("io.grpc:grpc-stub:$grpcVersion")
  runtimeOnly("io.grpc:grpc-netty:$grpcVersion")
  runtimeOnly("io.grpc:grpc-core:$grpcVersion")

  implementation("io.grpc:grpc-api:$grpcVersion")
  implementation("io.grpc:grpc-kotlin-stub:1.3.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutinesVersion")

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutinesVersion")
  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("io.grpc:grpc-testing:$grpcVersion")
}

sourceSets {
  main {
    java {
      setSrcDirs(
        listOf(
          "../../../java/src/main/java/com/google/flatbuffers",
          "../../../grpc/flatbuffers-java-grpc/src/main/java/com/google/flatbuffers/grpc",
        )
      )
    }
  }
  create("gen") {
    java {
      setSrcDirs(listOf("../../../tests/MyGame"))
      include("**/*.kt")
      compileClasspath += sourceSets["main"].output + sourceSets["main"].compileClasspath
      runtimeClasspath += sourceSets["main"].output + sourceSets["main"].runtimeClasspath
    }
  }
  test {
    java {
      setSrcDirs(listOf("."))
      compileClasspath += sourceSets["gen"].output
      runtimeClasspath += sourceSets["gen"].output
      include("**/*.kt")
    }
  }
}

tasks {
  wrapper {
    gradleVersion = "8.8" // version required
  }

  test {
    testLogging.showExceptions = true
    testLogging.showStackTraces = true
    testLogging.showStandardStreams = true
    useJUnit()
  }
}
