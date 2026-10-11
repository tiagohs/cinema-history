plugins {
    // Versões fixadas em gradle/libs.versions.toml; aplicadas só nos módulos.
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}
