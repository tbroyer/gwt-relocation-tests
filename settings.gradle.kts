import java.io.FileFilter
import org.gradle.util.GUtil

rootProject.name = "gwt-relocation-tests"

for (dir in file("projects").listFiles(FileFilter { it.isDirectory && it.resolve("build.gradle.kts").exists() })) {
    include(dir.name)
    project(":${dir.name}").projectDir = dir
}
