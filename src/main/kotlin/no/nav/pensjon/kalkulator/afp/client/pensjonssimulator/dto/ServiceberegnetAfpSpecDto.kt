package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import java.time.LocalDate

data class ServiceberegningAfpSpecDto(
    val uttakFom: LocalDate,
    val personopplysninger: PersonopplysningerDto,
    var opptjeningListe: List<OpptjeningFolketrygdenDataDto>
)

data class PersonopplysningerDto(
    val pid: String? = null,
    val foedselsdato: LocalDate? = null,
    val angittAfpOrdning: String? = null, // AfpTypeDto
    val flyktning: Boolean? = null,
    val antallAarUtenlands: Int? = null,
    val utenlandsoppholdListe: List<UtlandSpecDto>? = null,
    val forventetArbeidsinntekt: Int? = null,
    val inntektMaanedenFoerAfp: Int? = null,
    val eps: EpsDataDto? = null
)

data class EpsDataDto(
    val relasjon: RelasjonDto,
    val angittSivilstatus: String ? = null, // SivilstatusDto
    val registrertSivilstand: String ? = null, // SivilstandDto
    val mottarPensjon: Boolean? = null,
    val harInntektOver1G: Boolean? = null,
    val harInntektOver2G: Boolean? = null,
    val tidligereGiftEllerBarnMedSamboer: Boolean? = null
)

data class OpptjeningFolketrygdenDataDto(
    val aar: Int? = null,
    val pensjonsgivendeInntekt: Int? = null,
    val omsorgspoeng: Double? = null,
    val registrertePensjonspoeng: Double? = null,
    val maxUfoeregrad: Int? = null
)

data class StatsborgerDto(
    val pid: String? = null,
    val statsborgerskap: String ? = null // LandkodeEnum
)

data class RelasjonDto(
    val fom: LocalDate? = null,
    val person: StatsborgerDto? = null
)

data class UtlandSpecDto(
    val fom: LocalDate,
    val tom: LocalDate?,
    val land: String, // LandkodeEnum
    val arbeidetUtenlands: Boolean
)