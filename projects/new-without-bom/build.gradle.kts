plugins {
    `java`
}

val gwtVersion = findProperty("gwt.version") ?: "2.10.0"
dependencies {
    implementation("org.gwtproject:gwt-user:$gwtVersion")
    implementation("org.gwtproject:gwt-dev:$gwtVersion")

    fun addConditionally(dependency: String, booleanPropName: String, versionPropName: String, defaultVersion: String) {
        val booleanProp = (findProperty(booleanPropName) as? String)?.toBoolean() ?: false
        val versionProp = findProperty(versionPropName)
        if (booleanProp || versionProp != null) {
            implementation("$dependency:${versionProp ?: defaultVersion}")
        }
    }

    addConditionally("org.gwtproject.test:lib-with-user", "lib-with-user", "lib-with-user.version", "2.0.0")
    addConditionally("org.gwtproject.test:lib-with-dev", "lib-with-dev", "lib-with-dev.version", "2.0.0")
    addConditionally("org.gwtproject.test:lib-with-dev-only", "lib-with-dev-only", "lib-with-dev-only.version", "2.0.0")
}
