package no.nav.pensjon.kalkulator.tech.env

object EnvironmentUtil {

    fun isDevelopment(): Boolean =
        System.getenv()["NAIS_CLUSTER_NAME"] == "dev-gcp"

    fun isProduction(): Boolean =
        isDevelopment().not()
}