import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val mockkVersion = "1.13.17"
val tokenSupportVersion = "5.0.19"
val springdocVersion = "1.8.0"
val navFoedselsnummerVersion = "1.0-SNAPSHOT.6"
val kontrakterVersion = "3.0_20250312084645_ae52997"
val mainClass = "no.nav.familie.ef.infotrygd.Main"

plugins {
    val kotlinVersion = "2.1.20"
    val springBootVersion = "3.4.3"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.cyclonedx.bom") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Github"
        url = uri("https://maven.pkg.github.com/navikt/nav-foedselsnummer")
        credentials {
            username = "x-access-token" // project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
            password = System.getenv("GPR_API_KEY") ?: System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
        }
    }
}

dependencies {

    implementation("nav-foedselsnummer:core:$navFoedselsnummerVersion")
    testImplementation("nav-foedselsnummer:testutils:$navFoedselsnummerVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("net.ttddyy:datasource-proxy:1.10.1")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("no.nav.familie.kontrakter:enslig-forsorger:$kontrakterVersion")
    implementation("no.nav.familie.kontrakter:felles:$kontrakterVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.oracle.database.jdbc:ojdbc8:23.7.0.25.01")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.10.0")
    testImplementation("org.testcontainers:oracle-xe:1.20.6")
    testImplementation("com.h2database:h2")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("io.mockk:mockk-jvm:$mockkVersion")
}

val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

ktlint {
    version = "1.5.0"
}

tasks.ktlintMainSourceSetCheck {
    enabled = false
}

tasks.ktlintKotlinScriptCheck {
    enabled = false
}

tasks.withType<KotlinJvmCompile> {
    dependsOn(tasks.ktlintFormat)
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

tasks.test {
    jvmArgs = listOf("-Dnet.bytebuddy.experimental=true")
}

tasks.cyclonedxBom {
    setIncludeConfigs(listOf("runtimeClasspath", "compileClasspath"))
}
