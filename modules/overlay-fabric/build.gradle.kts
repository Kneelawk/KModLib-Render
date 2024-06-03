plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyFabricLoaderDependency()
    applyFabricApiDependency()
    applyXplatConnection(":overlay-xplat", "fabric")
    setupJavadoc()
}

kpublish {
    createPublication()
}
