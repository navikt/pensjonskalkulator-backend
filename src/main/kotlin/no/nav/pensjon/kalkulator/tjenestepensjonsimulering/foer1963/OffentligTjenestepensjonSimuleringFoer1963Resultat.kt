package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.FeilkodeDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.YtelsekodeFoer1963Dto
import java.time.LocalDate

data class OffentligTjenestepensjonSimuleringFoer1963Resultat(
    val tpnr: String? = null,
    val navnOrdning: String? = null,
    val utbetalingsperioder: List<UtbetalingsperiodeResultat> = emptyList(),
    val feilrespons: Feilrespons? = null
)

data class UtbetalingsperiodeResultat(
    val alderFom: Alder,
    val alderTom: Alder? = null,
    val grad: Int? = null,
    val arligUtbetaling: Double? = null,
    val ytelsekode: YtelseskodeFoer1963? = null,
    val mangelfullSimuleringkode: String? = null
)


enum class YtelseskodeFoer1963(val externalValue: YtelsekodeFoer1963Dto) {
    AP(YtelsekodeFoer1963Dto.AP), // Alderspensjon (tjenestepensjon)
    AFP(YtelsekodeFoer1963Dto.AFP), // Avtalefestet pensjon
    SERALDER(YtelsekodeFoer1963Dto.SERALDER); //Alderspensjon med særaldersgrense

    companion object {
        fun fromExternalValue(externalValue: YtelsekodeFoer1963Dto) = entries.first { it.externalValue == externalValue }
    }
}

data class Feilrespons(
    val feilkode: Feilkode,
    val feilmelding: String,
)

enum class Feilkode(val externalValue: FeilkodeDto) {
    KUNNE_IKKE_SIMULERE(FeilkodeDto.KUNNE_IKKE_SIMULERE),
    UKJENT_PRODUKT(FeilkodeDto.UKJENT_PRODUKT),
    MIDLERTIDIG_TEKNISK_FEIL(FeilkodeDto.MIDLERTIDIG_TEKNISK_FEIL),
    BEREGNING_GIR_NULL_UTBETALING(FeilkodeDto.BEREGNING_GIR_NULL_UTBETALING),
    OPPFYLLER_IKKE_INNGANGSVILKAAR(FeilkodeDto.OPPFYLLER_IKKE_INNGANGSVILKAAR),
    ANNEN_FEIL(FeilkodeDto.ANNEN_FEIL);

    companion object {
        fun fromExternalValue(externalValue: FeilkodeDto) = entries.first { it.externalValue == externalValue }
    }
}