package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.Sivilstatus
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid

object NorskPensjonSpecMapper {

    /**
     * NB: Norsk Pensjon's documentation says that 14 represents "livsvarig".
     * However, using 14 makes Norsk Pensjon return error "No signature in message!".
     * As a workoround the value 13 is used instead (although this represents "13 years" instead of "livsvarig").
     */
    private const val ANTALL_AAR_REPRESENTING_LIVSVARIG = 13
    private const val DEFAULT_HAR_EPS_PENSJON = true // Norsk Pensjon default
    private const val DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G = true // Norsk Pensjon default

    fun toDto(spec: PensjonsavtaleSpec, pid: Pid) =
        NorskPensjonSpecDto(
            foedselsnummer = pid.value,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttak,
            uttaksperioder = spec.uttaksperioder.map(::uttaksperiodeSpecDto),
            antallInntektsaarEtterUttak = antallInntektAarUnderHeltUttak(spec.uttaksperioder),
            harAfp = false, // avoids Norsk Pensjon calling Nav's AFP simulation
            harEpsPensjon = spec.harEpsPensjon ?: DEFAULT_HAR_EPS_PENSJON,
            harEpsPensjonsgivendeInntektOver2G = spec.harEpsPensjonsgivendeInntektOver2G
                ?: DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G,
            antallAarIUtlandetEtter16 = 0, // only relevant if oenskesSimuleringAvFolketrygd = true
            sivilstatus = Sivilstatus.fromInternalValue(spec.sivilstatus),
            oenskesSimuleringAvFolketrygd = false
        )

    private fun antallInntektAarUnderHeltUttak(perioder: List<UttaksperiodeSpec>): Int {
        val heltUttakPeriode = perioder.firstOrNull { it.grad == Uttaksgrad.HUNDRE_PROSENT } ?: return 0

        return if (heltUttakPeriode.aarligInntekt == null) 0
        else heltUttakPeriode.aarligInntekt.tomAlder
            ?.let { (it.aar - heltUttakPeriode.startAlder.aar).coerceAtMost(ANTALL_AAR_REPRESENTING_LIVSVARIG) }
            ?: ANTALL_AAR_REPRESENTING_LIVSVARIG
    }

    private fun uttaksperiodeSpecDto(spec: UttaksperiodeSpec) =
        NorskPensjonUttaksperiodeSpec(
            startAlder = spec.startAlder.aar,
            startMaaned = spec.startAlder.maaneder,
            grad = spec.grad.prosentsats,
            aarligInntekt = spec.aarligInntekt?.aarligBeloep ?: 0
        )
}