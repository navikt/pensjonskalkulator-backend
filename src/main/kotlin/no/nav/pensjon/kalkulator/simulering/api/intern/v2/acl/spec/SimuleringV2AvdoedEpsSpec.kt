package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.AvdoedEps
import java.time.LocalDate

/**
 * Informasjon om avdød ektefelle/partner/samboer (EPS) er relevant for pensjon med gjenlevenderett.
 */
data class SimuleringV2AvdoedEpsSpec(
    @field:Schema(description = "Person-identifikasjon (fødselsnummer e.l.)")
    @field:NotNull
    val pid: String,

    @field:Schema(description = "Dødsdato")
    @field:NotNull
    val doedsdato: LocalDate,

    @field:Schema(description = "Hvorvidt avdøde var medlem av folketrygden")
    val medlemAvFolketrygden: Boolean? = null,

    @field:Schema(description = "Avdødes inntekt før dødsdatoen (beløp i norske kroner)")
    val inntektFoerDoedBeloep: Int? = null,

    @field:Schema(description = "Hvorvidt avdøde hadde inntekt over grunnbeløpet (1G)")
    val inntektErOverGrunnbeloepet: Boolean? = null,

    @field:Schema(description = "Antall år avdøde hadde oppholdt seg i utlandet")
    val antallAarUtenlands: Int? = null
) {
    fun toInternalValue() =
        AvdoedEps(
            pid = Pid(pid),
            doedsdato = doedsdato,
            medlemAvFolketrygden = medlemAvFolketrygden == true,
            inntektFoerDoedBeloep = inntektFoerDoedBeloep ?: 0,
            inntektErOverGrunnbeloepet = inntektErOverGrunnbeloepet == true,
            antallAarUtenlands = antallAarUtenlands ?: 0
        )
}