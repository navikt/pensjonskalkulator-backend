package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.simulering.*

/**
 * DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av pensjon.
 */
data class SimuleringV2Spec(
    @field:Schema(description = "Type simulering")
    @field:NotNull
    val simuleringstype: SimuleringV2SimuleringstypeSpec,

    @field:Schema(description = "Årlig inntekt før start av pensjonsuttak (beløp i norske kroner)")
    val aarligInntektFoerUttakBeloep: Int? = null,

    @field:Schema(description = "Informasjon om gradert uttak")
    val gradertUttak: SimuleringV2GradertUttakSpec? = null, // default er helt uttak (100 %)

    @field:Schema(description = "Informasjon om helt uttak")
    @field:NotNull
    val heltUttak: SimuleringV2HeltUttakSpec,

    @field:Schema(description = "Liste over perioder der personen har oppholdt seg i utlandet")
    val utenlandsperiodeListe: List<SimuleringV2UtenlandsperiodeSpec>? = null,

    @field:Schema(description = "Sivilstatus")
    val sivilstatus: CommonV1Sivilstatus? = null,

    @field:Schema(description = "Informasjon om ektefelle/partner/samboer")
    val eps: SimuleringV2EpsSpec? = null,

    @field:Schema(description = "Informasjon om AFP i offentlig sektor")
    val offentligAfp: SimuleringV2OffentligAfpSpec? = null
) {
    fun toInternalValue(tillatSenereFoersteuttakForUfoere: Boolean) =
        ImpersonalSimuleringSpec(
            simuleringType = simuleringstype.internalValue,
            eps = eps?.toInternalValue() ?: EpsSpec.default,
            forventetAarligInntektFoerUttak = aarligInntektFoerUttakBeloep,
            sivilstatus = sivilstatus?.internalValue,
            gradertUttak = gradertUttak?.toInternalValue(),
            heltUttak = heltUttak.toInternalValue(),
            utenlandsopphold = utenlandsopphold(),
            afpInntektMaanedFoerUttak = offentligAfp?.harInntektMaanedenFoerUttak,
            afpOrdning = offentligAfp?.afpOrdning?.internalValue,
            innvilgetLivsvarigOffentligAfp = offentligAfp?.innvilgetLivsvarigAfpListe?.firstOrNull()?.toInternalValue(),
            inntektForrigeKalenderaar = offentligAfp?.inntektForrigeKalenderaar,
            inntektFremTilUttak = offentligAfp?.inntektFremTilUttak,
            inntektMaanedFoerAfp = offentligAfp?.inntektMaanedFoerAfp,
            tillatSenereFoersteuttakForUfoere = tillatSenereFoersteuttakForUfoere
        )

    private fun utenlandsopphold() =
        Utenlandsopphold(
            periodeListe = utenlandsperiodeListe.orEmpty().map { it.toInternalValue() },
            antallAar = 0 // irrelevant når 'periodeListe' er angitt
        )
}