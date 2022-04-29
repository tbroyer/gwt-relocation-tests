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

### Experiment #4

The `experiment-4` repository is the same as `experiment-3`
with an additional relocation from `org.gwtproject` to `com.google.gwt` for version `2.9.0`
(the `org.gwtproject:gwt:2.9.0` BOM doesn't relocate but references both `com.google.gwt` and `org.gwtproject` artifacts,
just like the `com.google.gwt:gwt:2.10.0` BOM in reverse).

### Experiment #5

The `experiment-5` repository is the same as `experiment-4`
with additional [Gradle Module Metadata](https://github.com/gradle/gradle/blob/3a013ff057b0db62cd05215abee49cedc4d05355/subprojects/docs/src/docs/design/gradle-module-metadata-latest-specification.md) for the libraries,
as if they had been deployed with Gradle and using `api(platform("<the GWT BOM>"))`.

In addition, version `2.10.0` upgrades Jetty, ASM, and HtmlUnit,
and removes the dependency on Ant.

### Experiment #6

The `experiment-6` repository is the same as `experiment-5`
with additional Gradle Module Metadata for `org.gwtproject:gwt-user:2.10.0` and `org.gwtproject:gwt-dev:2.10.0`,
except without Grade Module Metadata for the libraries.

### Experimet #7

The `experiment-7` repository is the same as `experiment-2`
with an additional 2.11.0 version for `org.gwtproject`.
The BOM references `com.google.gwt:*:2.10.0` to test behavior for the next release,
without having to release `com.google.gwt` again.
Libraries have a version 3.0.0 depending on 2.11.0.

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

As expected, results are similar to those of `experiment-1`, with one notable exception:
using the `com.google.gwt:gwt:2.10.0` BOM no longer generates errors,
and instead (as expected), works exactly the same as using the `org.gwtproject:gwt:2.10.0` BOM (as they have the same content).
In the `old-with-bom` project, upgrading the version through `gwt.version=2.10.0`, dependencies are now automatically relocated (along with a warning in Maven).

### Experiment #3

As expected, results are similar to those of `experiment-2`,
there are however notable changes with Gradle.

In the `old-without-bom` project with `lib-with-user.version=2.0.0`,
with Gradle `com.google.gwt:gwt-user` is automatically upgraded to `org.gwtproject:gwt-user:2.10.0`,
just as if everything was still at the same `com.google.gwt` groupId
(and creating a mismatch between `gwt-user` and `gwt-dev`, but this is expected here,
and again just as if everything was still at `com.google.gwt`).  
With Maven, due to the ["nearest definition" rule](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html), things work as in `experiment-2` though
(and without warning as the transitive `com.google.gwt:gwt-user:2.10.0` is ignored/superseded before even being resolved).

The `new-with-bom` and `new-without-bom` projects behave just like with `experiment-2`,
the transitive relocation to the referencing dependency don't cause any problem,
not even a warning in Maven.  
Gradle displays the `com.google.gwt` dependencies in its dependency graph
but the dependency/relocation cycle doesn't cause any problem.

The `new-without-bom` project with `lib-with-user.version=1.0.0` will automatically upgrade the transitive `com.google.gwt:gwt-user:2.9.0` to `org.gwtproject:gwt-user:2.10.0` with Gradle.  
With Maven, unfortunately, despite the dependency to the relocating `com.google.gwt:gwt-user:2.10.0` coming earlier (through `org.gwtproject:gwt-user:2.10.0`) than `com.google.gwt:gwt-user:2.9.0` (through `org.gwtproject.test:lib-with-user:1.0.0`),
and at the same depth, there will still be a mix of `com.google.gwt` and `org.gwtproject`.

### Experiment #4

As expected, results are similar to those of `experiment-3`,
with a notable change with Maven.

Using the `org.gwtproject:gwt:2.9.0` BOM helps stay on GWT 2.9.0
without risking mixed `com.google.gwt` and `org.gwtproject` dependencies,
by automatically downgrading the latter (to `2.9.0`, which relocates to the former).

There does not seem to be any downside to this "old version relocates to the old groupId, new version relocates to the new groupId",
but I haven't tested all combinations (particularly with Gradle, where dependency constraints are transitive, **and** published as part of [Gradle Module Metadata](https://docs.gradle.org/current/userguide/publishing_gradle_module_metadata.html)).

### Experiment #5

As expected, results are similar to those of `experiment-4`,
with notable changes with Gradle.

Depending on a library that uses the BOM as a `platform`
brings the BOM dependency management as dependency constraints.
This has the effect of automatically upgrading both `gwt-user` and `gwt-dev`
even in projects that don't use a BOM and depend on a library,
even if that library only brings `gwt-user` or `gwt-dev`
(e.g. `old-with-bom` and `old-without-bom`, with `-Plib-with-user.version=2.0.0`).  
Due to Gradle's "highest version" rule,
this won't however downgrade dependencies in a reverse setup
(e.g. `new-with-bom` or `new-without-bom`, with `-Plib-with-user.version=1.0.0`).

Projects that use the `org.gwtproject:gwt:2.9.0` BOM
(e.g. `new-with-bom` with `-Pgwt.version=2.9.0`)
continue to work OK,
even when they also depend on a library bringing `2.10.0`
(everything's upgraded to `2.10.0` then, both `gwt-user` and `gwt-dev`,
eliminating the risk of mixed `org.gwtproject` and `com.google.gwt` dependencies).  
The "old version relocates to the old groupId, new version relocates to the new groupId" doesn't seem to cause any issue then.

This suggests actually deploying Gradle Module Metadata for GWT itself
to make sure that (unless overridden) `gwt-user` and `gwt-dev` versions are aligned
whichever the setup in consuming projects.
This also means that, if available, BOMs should probably try to import other BOMs
rather than adding dependency management for discrete artifacts
(e.g. Jetty or ASM; though Jetty only has a BOM starting with `9.3.26.v20190403`, and ASM doesn't have one).

### Experiment #6

As expected, results are similar to those of `experiment-4`,
with notable changes with Gradle.

Whether you use a BOM or not,
depending on a library that brings a newer version of GWT
automatically upgrades both `gwt-user` and `gwt-dev`,
even if the library depended only on one of them.
