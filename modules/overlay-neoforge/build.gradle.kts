plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyNeoforgeDependency()
    applyXplatConnection(":overlay-xplat", "neoforge")
    setupJavadoc()
}

kpublish {
    createPublication()
}
