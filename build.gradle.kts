plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatform)
}

group = "com.k.pmpstudy"
version = "1.2.0"

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
              <li>Add <b>Copy Short Revision Number</b> to the Git Log branches popup
                  (under the <i>Checkout</i> group) &mdash; copies the abbreviated hash of
                  the selected branch, tag, or HEAD (Current Branch).</li>
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
