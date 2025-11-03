package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto

import com.fasterxml.jackson.annotation.JsonFormat
import no.nav.pensjon.kalkulator.person.Pid
import java.time.LocalDate

data class SimulerOffentligTjenestepensjonFoer1963Dto(
    val simuleringEtter2011: SimuleringEtter2011Dto
)

data class SimuleringEtter2011Dto(
    val simuleringType: String?, //SimuleringTypeCode i pen
    val fnr: String?,
    val fnrAvdod: Pid?,
    val samtykke: Boolean?,
    val forventetInntekt: Int?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val forsteUttakDato: LocalDate?,
    val utg: String?, //UttaksgradCode i pen
    val inntektUnderGradertUttak: Int?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val heltUttakDato: LocalDate?,
    val inntektEtterHeltUttak: Int?,
    val antallArInntektEtterHeltUttak: Int?,
    val utenlandsopphold: Int?,
    val flyktning: Boolean?,
    val sivilstatus: String?, //SivilstatusTypeCode i pen
    val epsPensjon: Boolean?,
    val eps2G: Boolean?,
    val afpOrdning: String?, //AfpOrdningTypeCode i pen
    val afpInntektMndForUttak: Int?,
    val stillingsprosentOffHeltUttak: String?, //StillingsprOffCode i pen
    val stillingsprosentOffGradertUttak: String?, //StillingsprOffCode i pen
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val dodsdato: LocalDate?,
    val avdodAntallArIUtlandet: Int?,
    val avdodInntektForDod: Int?,
    val inntektAvdodOver1G: Boolean?,
    val avdodMedlemAvFolketrygden: Boolean?,
    val avdodFlyktning: Boolean?,
    val simulerForTp: Boolean?,
    val utenlandsperiodeForSimuleringList: List<UtenlandsperiodeForSimuleringDto>?,
    val fremtidigInntektList: List<FremtidigInntektDto>?
)

data class UtenlandsperiodeForSimuleringDto(
    val land: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val periodeFom: LocalDate? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val periodeTom: LocalDate? = null,
    val pensjonType: String? = null
)

data class FremtidigInntektDto(
    val aar: Int? = null,
    val belop: Int? = null
)