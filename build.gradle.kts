import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockkVersion = "1.13.8"
val tokenSupportVersion = "2.1.6"
val springdocVersion = "1.7.0"
val navFoedselsnummerVersion = "1.0-SNAPSHOT.6"
val kontrakterVersion = "3.0_20240109111848_d97569f"
val fellesVersion = "1.20221006150009_46021ed"
val mainClass = "no.nav.familie.ef.infotrygd.Main"
val ktlint by configurations.creating

plugins {
    val kotlinVersion = "1.9.22"
    val springBootVersion = "2.7.5"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
// id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

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
            username = "x-access-token" //project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
            password = System.getenv("GPR_API_KEY") ?: System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String?
        }
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencies {


    ktlint("com.pinterest:ktlint:0.51.0-FINAL") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
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
    implementation("net.ttddyy:datasource-proxy:1.10")
    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    implementation("no.nav.familie.kontrakter:enslig-forsorger:$kontrakterVersion")
    implementation("no.nav.familie.kontrakter:felles:$kontrakterVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    implementation("javax.inject:javax.inject:1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.oracle.database.jdbc:ojdbc8:23.3.0.23.09")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.2")
    testImplementation("org.testcontainers:oracle-xe:1.19.3")
    testImplementation("com.h2database:h2")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("io.mockk:mockk-jvm:$mockkVersion")

    // overskrever spring sin versjon, blir brukt av mock-oauth2-server
    testImplementation("com.squareup.okhttp3", "mockwebserver").version {
        strictly("4.10.0")
    }
    testImplementation("com.squareup.okhttp3", "okhttp").version {
        strictly("4.9.2")
    }

}

val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    // outputs.dir(outputDir)

    description = "Check Kotlin code style."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
    inputs.files(inputFiles)
    // outputs.dir(outputDir)

    description = "Fix Kotlin code style deviations."
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/**/*.kt")
    jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.withType<KotlinCompile> {
    dependsOn("ktlintFormat")
    dependsOn("ktlintCheck")
    tasks.findByName("ktlintCheck")?.mustRunAfter("ktlintFormat")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

extensions.findByName("buildScan")?.withGroovyBuilder {
    setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
    setProperty("termsOfServiceAgree", "yes")
}

// tasks.findByName('publish').mustRunAfter 'build'