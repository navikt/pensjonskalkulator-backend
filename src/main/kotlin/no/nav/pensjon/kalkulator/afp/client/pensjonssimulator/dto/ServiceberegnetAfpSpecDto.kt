package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto

import java.time.LocalDate

data class ServiceberegnetAfpSpecDto(
    val simuleringstype: String,
    val uttaksdato: LocalDate,
    val personopplysninger: PersonopplysningerDto,
    val barneopplysninger: Any?,
    val opptjeningFolketrygden: OpptjeningFolketrygdenDto
)

data class PersonopplysningerDto(
    val ident: String,
    val fodselsdato: LocalDate,
    val valgtAfpOrdning: String,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val erUnderUtdanning: Boolean?,
    val epsData: Any?,
    val avdodList: List<Any>
)

data class OpptjeningFolketrygdenDto(
    val egenOpptjeningFolketrygden: List<OpptjeningAarDto>,
    val avdodesOpptjeningFolketrygden: List<Any>,
    val morsOpptjeningFolketrygden: List<Any>,
    val farsOpptjeningFolketrygden: List<Any>
)

data class OpptjeningAarDto(
    val ar: Int?,
    val pensjonsgivendeInntekt: Int?,
    val omsorgspoeng: Double?,
    val maksUforegrad: Int?,
    val registrertePensjonspoeng: Double?
)
