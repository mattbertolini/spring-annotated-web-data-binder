package com.mattbertolini.buildlogic

import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("net.ltgt.errorprone")
}

val libsCatalog = versionCatalogs.named("libs")
val errorProneCore = libsCatalog.findLibrary("errorProneCore").orElseThrow()
val errorProneAnnotations = libsCatalog.findLibrary("errorProneAnnotations").orElseThrow();
val nullAway = libsCatalog.findLibrary("nullAway").orElseThrow()
val nullAwayAnnotations = libsCatalog.findLibrary("nullAwayAnnotations").orElseThrow()

dependencies {
    errorprone(errorProneCore)
    errorprone(nullAway)
    add("compileOnly", errorProneAnnotations)
    add("compileOnly", nullAwayAnnotations)
}

tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "com.mattbertolini")
    }
}
