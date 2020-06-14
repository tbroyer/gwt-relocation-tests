val testRepository = findProperty("test-repository") ?: "experiment-1"
subprojects {
    repositories {
        maven(url = "https://tbroyer.github.io/gwt-relocation-tests/repositories/$testRepository") {
            mavenContent {
                releasesOnly()
            }
        }
        mavenCentral()
    }
}
