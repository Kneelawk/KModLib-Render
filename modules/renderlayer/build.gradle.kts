plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyFabricLoaderDependency()
    applyFabricApiDependency()
    setupJavadoc()
}

loom {
    accessWidenerPath.set(file("src/main/resources/kmodlib-renderlayer.accesswidener"))
}

kpublish {
    createPublication()
}
