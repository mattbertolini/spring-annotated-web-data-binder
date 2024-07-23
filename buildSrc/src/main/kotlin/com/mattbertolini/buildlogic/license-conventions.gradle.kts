package com.mattbertolini.buildlogic

import java.time.LocalDate

plugins {
    id("com.github.hierynomus.license")
}

license {
    mapping("java", "SLASHSTAR_STYLE")
    header = isolated.rootProject.projectDirectory.file("LICENSE_HEADER.txt").asFile
    ext.set("year", LocalDate.now().year)
}
