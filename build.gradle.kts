plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatform)
}

group = "com.k.pmpstudy"
version = "1.0.1"

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        intellijIdea(providers.gradleProperty("platformVersion"))
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        bundledPlugin("Git4Idea")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }

        changeNotes = """
            <h3>${project.version}</h3>
            <ul>
              <li>Add <b>Copy URL(Short Revision Number)</b> to the Git Log context menu &mdash;
                  copies the GitHub commit URL using the short revision hash.</li>
              <li>Add <b>GitHub Repository URL(Short Revision)</b> to <i>Copy Path/Reference&hellip;</i>
                  and the editor's <i>Copy / Paste Special</i> submenu &mdash; copies the GitHub URL of
                  the selected file/directory at the current commit. Includes <code>#L&lt;n&gt;</code> or
                  <code>#L&lt;n&gt;-L&lt;m&gt;</code> when invoked from the editor.</li>
              <li>GitHub link actions are hidden automatically when no <code>github.com</code> remote
                  is configured.</li>
            </ul>
        """.trimIndent()
    }
    publishing {
        token = providers.gradleProperty("JETBRAIN_TOKEN")
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}
