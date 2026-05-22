package no.nav.pensjon.kalkulator.lagring

data class ForbeholdInnhold(
    val seksjoner: List<ForbeholdSeksjon>
)

data class ForbeholdSeksjon(
    val tittel: String?,
    val avsnitt: List<ForbeholdAvsnitt>
)

data class ForbeholdAvsnitt(
    val tekst: String,
    val punktliste: List<String>?
)
