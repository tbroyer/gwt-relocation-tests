# gwt-relocation-tests

This repository is meant to test different strategies for relocating GWT's Maven artifacts from `com.google.gwt` to `org.gwtproject`,
by hosting Maven repositories served by GitHub Pages,
as well as sample projects testing several combinations of old and new dependencies.

## Repositories

### Experiment #1

The `experiment-1` repository contains `com.google.gwt` POMs all relocating (using [Maven's relocation](https://maven.apache.org/guides/mini/guide-relocation.html)) to their `org.gwtproject` equivalent.
This includes the `com.google.gwt:gwt` BOM relocating to the `org.gwtproject:gwt` BOM.

The `org.gwtproject:gwt` BOM links to both the (relocated) `com.google.gwt` and the `org.gwtproject` artifacts.

The expectation is that a project using the new BOM will automatically see its transitive dependencies to `com.google.gwt` artifacts be relocated to their `org.gwtproject` equivalent.

The repository also includes libraries with dependencies on `gwt-user`, `gwt-dev`, or both.
Each library comes in two versions: `1.0.0` depends on `com.google.gwt:*:2.9.0` whereas `2.0.0` has upgraded to `org.gwtproject:*:2.10.0`,
this should make it possible to test various combinations (depending on whether the `2.0.0` is included in the dependency graph or not).

## Projects

When running Maven commands, `-Dmaven.repo.local=.repository` is automatically used to avoid polluting your local repository and make things easier to clean
(at the expense of having to re-download many Maven plugins).
For Gradle, pass `--gradle-user-home …` to achieve something similar.

You can switch repository by setting the `test-repository` property (`-Dtest-repository=…` with Maven, `-Ptest-repository=…` with Gradle);
the default value is `experiment-1`.

Maven projects are all independent from one another,
whereas Gradle projects are all subprojects of the project at the root of the repository.

To test dependency graphs, use
```sh
mvn -f projects/<project>/pom.xml dependency:tree
```
or
```sh
./gradlew --gradle-user-home=.gradle-user-home :<project>:dependencies
```

### old-with-bom

This project uses the `com.google.gwt:gwt` BOM for dependency management.
The version defaults to `2.9.0` but can be changed through the `gwt.version` property
(`-Dgwt.version=…` for Maven, `-Pgwt.version=…`).

By default, it only depends on `gwt-user` and `gwt-dev`, but dependencies to test libraries can be added dynamically.

With Maven, this is done through [profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html).
 * The `lib-with-user` profile will add a dependency on `org.gwtproject.test:lib-with-user`.
   The dependency defaults to version `1.0.0` but this can be changed through the `lib-with-user.version` property.
   Setting the property automatically triggers the profile to make things easier.
 * the `lib-with-dev` profile will add a dependency on `org.gwtproject.test:lib-with-dev`,
   and works the exact same way as the above, with the `lib-with-dev.version` property.
 * the `lib-with-dev-only` in turn works exactly the same.

With Gradle, this is done through project properties (`-P…=…`).
The boolean properties `lib-with-user`, `lib-with-dev`, and `lib-with-dev-only` are equivalent to activating the Maven profiles above.
The string properties `lib-with-user.version`, `lib-with-dev.version`, and `lib-with-dev-only.version` have the same meaning as the Maven properties above.

### new-with-bom

This project uses the `org.gwtproject:gwt` BOM for dependency management, defaulting to version `2.10.0`.

It works the same as `old-with-bom` except the test libraries default to their version `2.0.0`.

### old-without-bom

This project is the same as `old-with-bom` except it doesn't use the BOM but directly depends on `gwt-user` and `gwt-dev`.

### new-without-bom

This project is the same as `new-with-bom` except it doesn't use the BOM but directly depends on `gwt-user` and `gwt-dev`.
