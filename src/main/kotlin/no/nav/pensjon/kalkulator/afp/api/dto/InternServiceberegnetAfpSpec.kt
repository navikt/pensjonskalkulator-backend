package no.nav.pensjon.kalkulator.afp.api.dto

import java.time.LocalDate

data class InternServiceberegnetAfpSpec(
    val fodselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val afpOrdning: String,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val opptjeningFolketrygden: List<InternOpptjeningAar>
)

data class InternOpptjeningAar(
    val ar: Int,
    val pensjonsgivendeInntekt: Int?,
    val omsorgspoeng: Double?,
    val maksUforegrad: Int?,
    val registrertePensjonspoeng: Double?
)
