package no.nav.pensjon.kalkulator.simulering

data class SimulertPre2025OffentligAfp (
    val alderAar: Int,
    val totalbelopAfp: Int,
    val tidligereArbeidsinntekt: Int,
    val grunnbelop: Int,
    val sluttpoengtall: Double,
    val trygdetid: Int,
    val poeangaarFoer92: Int,
    val poeangaarEtter91: Int,
    val grunnpensjon: Int,
    val tilleggspensjon: Int,
    val afpTillegg: Int,
    val sertillegg: Int
)