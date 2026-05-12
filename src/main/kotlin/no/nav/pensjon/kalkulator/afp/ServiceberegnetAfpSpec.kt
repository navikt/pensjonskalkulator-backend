package no.nav.pensjon.kalkulator.afp

import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import java.time.LocalDate

data class ServiceberegnetAfpSpec(
    val uttaksdato: LocalDate,
    val fnr: String,
    val fodselsdato: LocalDate,
    val afpOrdning: AfpOrdningType,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val utenlandsopphold: List<Opphold>?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val opptjeningFolketrygden: List<OpptjeningAar>,
    val epsMottarPensjon: Boolean? = null,
    val epsInntektOver2G: Boolean? = null,
    val tidligereGiftEllerBarnMedSamboer: Boolean?,
    val sivilstatus: Sivilstatus? = null,
)

data class OpptjeningAar(
    val ar: Int,
    val pensjonsgivendeInntekt: Int?,
    val omsorgspoeng: Double?,
    val maksUforegrad: Int?,
    val registrertePensjonspoeng: Double?
)
