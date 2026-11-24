package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2Vilkaarsproevingsresultat(
    @field:Schema(description = "Hvorvidt 'kravet' om pensjon er innvilget")
    @field:NotNull
    val erInnvilget: Boolean,

    @field:Schema(description = "Et alternativ sett av uttaksparametre som vil resultere i innvilget pensjon (relevant når de opprinnelig angitte parametre ga avslag)")
    val alternativ: SimuleringV2Uttaksparametre? = null,

    @field:Schema(description = "Hvorvidt livsvarig AFP i offentlig sektor har betydning for innvilgelse av alderspensjon")
    val livsvarigOffentligAfpHarBetydningForInnvilgelse: Boolean? = null
) {
    companion object {
        fun transferable(source: Vilkaarsproeving) =
            SimuleringV2Vilkaarsproevingsresultat(
                erInnvilget = source.innvilget,
                alternativ = source.alternativ?.let(SimuleringV2Uttaksparametre::transferable),
                livsvarigOffentligAfpHarBetydningForInnvilgelse = source.livsvarigOffentligAfpHarBetydningForInnvilgelse
            )

        fun serverfeil() =
            SimuleringV2Vilkaarsproevingsresultat(erInnvilget = false)
    }
}