package com.kneelawk.submodule;

import java.util.List;
import java.util.Map;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
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
        Property<String> suffixProp = submodule.getSuffix();
        Provider<String> suffix = suffixProp.map(s -> "-" + s);
        Property<String> docsSuffixProp = submodule.getDocsSuffix();
        Provider<String> docsSuffix = docsSuffixProp.map(s -> "-" + s);

        isRoot.convention(false);
        docsSuffixProp.convention(suffixProp);

        String baseName = (String) project.property("base_name");
        if (baseName == null)
            throw new IllegalStateException("Submodule plugin requires the `base_name` property.");

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

        String publishRepo = System.getenv("PUBLISH_REPO");

        BasePluginExtension base = ext.getByType(BasePluginExtension.class);
        JavaPluginExtension java = ext.getByType(JavaPluginExtension.class);
        PublishingExtension publishing = ext.getByType(PublishingExtension.class);

        base.getArchivesName().convention(suffix.map(s -> baseName + s));

        // Setup minecraft dependency
        dependencies.add("minecraft", "com.mojang:minecraft:" + minecraftVersion);
//        dependencies.add("mappings", "net.fabricmc:yarn:" + yarnMappings + ":v2");

        tasks.named("processResources", Copy.class, pr -> {
            pr.getInputs().property("version", project.getVersion());

            pr.exclude("**/*.xcf");

            pr.filesMatching("fabric.mod.json", details -> details.expand(Map.of("version", project.getVersion())));
            pr.filesMatching("META-INF/mods.toml", details -> details.expand(Map.of("version", project.getVersion())));
        });

        // Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
        tasks.withType(JavaCompile.class).configureEach(compile -> compile.getOptions().getRelease().set(17));

        java.withJavadocJar();
        java.withSourcesJar();
        java.setSourceCompatibility(JavaVersion.VERSION_17);
        java.setTargetCompatibility(JavaVersion.VERSION_17);

        // Configure the maven repo
        if (publishRepo != null) {
            publishing.getRepositories().maven(repo -> {
                repo.setName("publishRepo");
                repo.setUrl(rootProject.uri(publishRepo));
            });
        }

        // Make builds reproducible
        tasks.withType(AbstractArchiveTask.class).configureEach(task -> {
            task.setPreserveFileTimestamps(false);
            task.setReproducibleFileOrder(true);
        });

        project.afterEvaluate(proj -> {
            tasks.named("genSources", task -> task.setDependsOn(List.of("genSourcesWithVineflower")));

            tasks.named("jar", Jar.class, jar -> jar.from(rootProject.file("LICENSE"),
                spec -> spec.rename(name -> name + "_" + base.getArchivesName().get())));

            if (!isRoot.get()) {
                BasePluginExtension rootBase = rootExt.getByType(BasePluginExtension.class);
                base.getLibsDirectory().convention(rootBase.getLibsDirectory());

                JavaPluginExtension rootJava = rootExt.getByType(JavaPluginExtension.class);
                java.getDocsDir().convention(
                    rootJava.getDocsDir().flatMap(docsDir -> docsSuffix.map(s -> docsDir.dir(baseName + s))));
            }

            // Configure the maven publication
            publishing.getPublications()
                .create("mavenJava", MavenPublication.class, pub -> {
                    pub.setArtifactId(baseName + suffix.get());
                    pub.from(project.getComponents().getByName("java"));
                });

            // Fix issue with just running `genSources`
//            tasks.named("genSourcesWithVineflower", task -> {
//                task.dependsOn(rootProject.getAllprojects().stream()
//                    .map(subProj2 -> subProj2.getTasks().named("resolveVineflower")).toArray());
//                task.dependsOn(
//                    rootProject.getAllprojects().stream().map(subProj2 -> subProj2.getTasks().named("unpickJar"))
//                        .toArray());
//            });
        });
    }
}
