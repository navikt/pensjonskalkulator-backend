package no.nav.pensjon.kalkulator.afp

import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import java.time.LocalDate

data class ServiceberegnetAfpSpec(
    val uttaksdato: LocalDate,
    val fnr: String,
    val fodselsdato: LocalDate,
    val afpOrdning: AfpOrdningType,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val opptjeningFolketrygden: List<OpptjeningAar>
)

data class OpptjeningAar(
    val ar: Int,
    val pensjonsgivendeInntekt: Int?,
    val omsorgspoeng: Double?,
    val maksUforegrad: Int?,
    val registrertePensjonspoeng: Double?
)
