package com.kneelawk.submodule;

import java.util.List;
import java.util.Map;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;

@SuppressWarnings("unused")
public class SubmodulePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        ExtensionContainer ext = project.getExtensions();
        TaskContainer tasks = project.getTasks();
        DependencyHandler dependencies = project.getDependencies();
        Project rootProject = project.getRootProject();
        ExtensionContainer rootExt = rootProject.getExtensions();

        SubmoduleExtension submodule = ext.create("submodule", SubmoduleExtension.class);
        Property<Boolean> isRoot = submodule.getRoot();
        Provider<String> suffix = submodule.getSuffix().map(s -> "-" + s);

        isRoot.convention(false);

        String archivesBaseName = (String) project.property("archives_base_name");
        if (archivesBaseName == null)
            throw new IllegalStateException("Submodule plugin requires the `archives_base_name` property.");

        String mavenGroup = (String) project.property("maven_group");
        if (mavenGroup == null)
            throw new IllegalStateException("Submodule plugin requires the `maven_group` property.");
        project.setGroup(mavenGroup);

        String minecraftVersion = (String) project.property("minecraft_version");
        if (minecraftVersion == null)
            throw new IllegalStateException("Submodule plugin requires the `minecraft_version` property.");

        String yarnMappings = (String) project.property("yarn_mappings");
        if (yarnMappings == null)
            throw new IllegalStateException("Submodule plugin requires the `yarn_mappings` property.");

        String loaderVersion = (String) project.property("loader_version");
        if (loaderVersion == null) throw new IllegalStateException("Submodule plugin requires the `loader_version` property.");

        String publishRepo = System.getenv("PUBLISH_REPO");

        BasePluginExtension base = ext.getByType(BasePluginExtension.class);
        JavaPluginExtension java = ext.getByType(JavaPluginExtension.class);
        PublishingExtension publishing = ext.getByType(PublishingExtension.class);

        base.getArchivesName().convention(suffix.map(s -> archivesBaseName + s));

        // Setup minecraft dependency
        dependencies.add("minecraft", "com.mojang:minecraft:" + minecraftVersion);
        dependencies.add("mappings", "net.fabricmc:yarn:" + yarnMappings + ":v2");
        // Fabric loader
        String fabricLoaderDep = "net.fabricmc:fabric-loader:" + loaderVersion;
        dependencies.add("modCompileOnly", fabricLoaderDep);
        dependencies.add("modLocalRuntime", fabricLoaderDep);

        tasks.named("processResources", Copy.class, pr -> {
            pr.getInputs().property("version", project.getVersion());

            pr.exclude("**/*.xcf");

            pr.filesMatching("fabric.mod.json", details -> details.expand(Map.of("version", project.getVersion())));
        });

        // Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
        tasks.withType(JavaCompile.class).configureEach(compile -> compile.getOptions().getRelease().set(17));

        java.withJavadocJar();
        java.withSourcesJar();
        java.setSourceCompatibility(JavaVersion.VERSION_17);
        java.setTargetCompatibility(JavaVersion.VERSION_17);

        // configure the maven publication
        publishing.getPublications()
            .create("mavenJava", MavenPublication.class, pub -> pub.from(project.getComponents().getByName("java")));
        if (publishRepo != null) {
            publishing.getRepositories().maven(repo -> {
                repo.setName("publishRepo");
                repo.setUrl(rootProject.uri(publishRepo));
            });
        }

        project.afterEvaluate(proj -> {
            tasks.named("genSources", task -> task.setDependsOn(List.of("genSourcesWithQuiltflower")));

            tasks.named("jar", Jar.class, jar -> jar.from(rootProject.file("LICENSE"),
                spec -> spec.rename(name -> name + "_" + base.getArchivesName().get())));

            if (!isRoot.get()) {
                BasePluginExtension rootBase = rootExt.getByType(BasePluginExtension.class);
                base.getLibsDirectory().convention(rootBase.getLibsDirectory());

                JavaPluginExtension rootJava = rootExt.getByType(JavaPluginExtension.class);
                java.getDocsDir().convention(
                    rootJava.getDocsDir().flatMap(docsDir -> suffix.map(s -> docsDir.dir(archivesBaseName + s))));
            }
        });
    }
}
