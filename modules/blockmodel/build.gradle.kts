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

dependencies {
    api(project(":rendertag", configuration = "namedElements"))
    include(project(":rendertag"))
}

kpublish {
    createPublication()
}
