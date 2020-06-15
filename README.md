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

### Experiment #2

The `experiment-2` repository is the same as `experiment-1`,
except the `com.google.gwt:gwt:2.10.0` BOM doesn't relocate,
and is instead an exact copy of the `org.gwtproject:gwt:2.10.0` BOM.

### Experiment #3

The `experiment-3` repository is the same as `experiment-2`,
except `org.gwtproject:gwt-user` and `org.gwtproject:gwt-dev` have a dependency on `com.google.gwt:gwt-user` and `com.google.gwt:gwt-dev` respectively
(those artifacts relocating to the former).

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

## Early conclusions

### Experiment #1

The `org.gwtproject:gwt` BOM from `experiment-1` works great for projects updated to use it,
and will automatically upgrade their dependencies to use `org.gwtproject` instead of `com.google.gwt`.

Projects not using the BOM run the risk of having a mix of `org.gwtproject` and `com.google.gwt` dependencies,
unless they add dependency management rules to upgrade `com.google.gwt` dependencies to the relocated version.

Projects still on GWT 2.9.0, whether they use the BOM or not,
run the risk of having a mix `org.gwtproject` and `com.google.gwt` dependencies,
unless they use exclusions on their dependencies that bring the `org.gwtproject` transitively.

Using the `com.google.gwt:gwt:2.10.0` BOM leads to errors,
as Maven doesn't automatically relocates it to `org.gwtproject:gwt:2.10.0`,
so it complains that the `gwt-user` and `gwt-dev` dependencies don't have a version.

Using the `com.google.gwt:*:2.10.0` dependencies automatically relocates to `org.gwtproject:*:2.10.0` with a warning.

Gradle works **exactly** the same as Maven here, except it won't warn when relocating
(and you have more options for resolving mixed cases).

### Experiment #2

As expected, results are similar to those the `experiment-1`, with one notable exception:
using the `com.google.gwt:gwt:2.10.0` BOM no longer generates errors,
and instead (as expected), works exactly the same as using the `org.gwtproject:gwt:2.10.0` BOM (as they have the same content).
In the `old-with-bom` project, upgrading the version through `gwt.version=2.10.0`, dependencies are now automatically relocated (along with a warning in Maven).
