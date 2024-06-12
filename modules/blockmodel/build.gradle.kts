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

    val codextra_version: String by project
    modApi("com.kneelawk.codextra:codextra-fabric:$codextra_version")
    include("com.kneelawk.codextra:codextra-fabric:$codextra_version")
}

kpublish {
    createPublication()
}
