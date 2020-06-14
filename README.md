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
