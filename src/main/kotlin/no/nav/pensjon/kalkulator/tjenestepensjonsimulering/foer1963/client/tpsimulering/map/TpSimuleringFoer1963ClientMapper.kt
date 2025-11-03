package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.SimuleringOffentligTjenestepensjonFoer1963Spec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.SimulerOffentligTjenestepensjonFoer1963Dto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.SimuleringEtter2011Dto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.UtenlandsperiodeForSimuleringDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto.SimulerTjenestepensjonFoer1963ResponseDto
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.UtbetalingsperiodeResultat
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object TpSimuleringFoer1963ClientMapper {

    fun fromDto(dto: SimulerTjenestepensjonFoer1963ResponseDto) =
        dto.simulertPensjonListe.firstOrNull()?.let { sim ->
            OffentligTjenestepensjonSimuleringFoer1963Resultat(
                tpnr = sim.tpnr,
                navnOrdning = sim.navnOrdning,
                utbetalingsperioder = sim.utbetalingsperioder?.map { periode ->
                    UtbetalingsperiodeResultat(
                        datoFom = periode.datoFom?.let(::epochMillisToLocalDate),
                        datoTom = periode.datoTom?.let(::epochMillisToLocalDate),
                        grad = periode.grad,
                        arligUtbetaling = periode.arligUtbetaling,
                        ytelsekode = periode.ytelsekode,
                        mangelfullSimuleringkode = periode.mangelfullSimuleringkode
                    )
                }
            )
        } ?: OffentligTjenestepensjonSimuleringFoer1963Resultat()

    private fun epochMillisToLocalDate(millis: Long): LocalDate =
        Instant.ofEpochMilli(millis).atZone(ZoneId.of("Europe/Oslo")).toLocalDate()

    fun toDto(spec: SimuleringOffentligTjenestepensjonFoer1963Spec, pid: Pid): SimulerOffentligTjenestepensjonFoer1963Dto {
        val gradertUttakFom: LocalDate? =
            spec.gradertUttak?.uttakFomAlder?.let {
                SimulatorAnonymAlderDato(alder(it), spec.foedselsdato).dato
            }

        val heltUttakFom: LocalDate =
            SimulatorAnonymAlderDato(alder = alder(spec.heltUttak.uttakFomAlder!!), spec.foedselsdato).dato

        val inntektTom: LocalDate =
            SimulatorAnonymAlderDato(alder = alder(spec.heltUttak.inntekt!!.tomAlder), spec.foedselsdato).dato

        return SimulerOffentligTjenestepensjonFoer1963Dto(
            SimuleringEtter2011Dto(
                simuleringType = SimulatorSimuleringType.fromInternalValue(spec.simuleringType).externalValue,
                fnr = pid.value,
                sivilstatus = SimulatorSivilstand.fromInternalValue(spec.sivilstand).externalValue,
                eps2G = spec.eps.harInntektOver2G,
                epsPensjon = spec.eps.harPensjon,
                forventetInntekt = spec.forventetAarligInntektFoerUttak,
                inntektUnderGradertUttak = spec.gradertUttak?.aarligInntekt,
                inntektEtterHeltUttak = spec.heltUttak.inntekt.aarligBeloep ?: 0,
                utenlandsperiodeForSimuleringList = spec.utenlandsopphold.periodeListe.map(::utenlandsperiode),
                afpInntektMndForUttak = 500000,
                afpOrdning = SimulatorAfpOrdningType.fromInternalValue(spec.afpOrdning).externalValue,
                stillingsprosentOffHeltUttak = spec.stillingsprosentOffHeltUttak,
                stillingsprosentOffGradertUttak = spec.stillingsprosentOffGradertUttak,
                forsteUttakDato = gradertUttakFom ?: heltUttakFom,
                heltUttakDato = heltUttakFom,
                antallArInntektEtterHeltUttak = inntektTom.year - heltUttakFom.year,
                fnrAvdod = null,
                samtykke = true,
                utg = "50",
                utenlandsopphold = spec.utenlandsopphold.antallAar ?: 0,
                flyktning = false,
                dodsdato = null,
                avdodAntallArIUtlandet = null,
                avdodInntektForDod = null,
                inntektAvdodOver1G = null,
                avdodMedlemAvFolketrygden = null,
                avdodFlyktning = null,
                simulerForTp = true,
                fremtidigInntektList = null
            )
        )
    }

    private fun penUttaksgradValue(grad: Uttaksgrad?): String? = when (grad) {
        null -> null
        Uttaksgrad.NULL -> "P_0"
        Uttaksgrad.TJUE_PROSENT -> "P_20"
        Uttaksgrad.FOERTI_PROSENT -> "P_40"
        Uttaksgrad.FEMTI_PROSENT -> "P_50"
        Uttaksgrad.SEKSTI_PROSENT -> "P_60"
        Uttaksgrad.AATTI_PROSENT -> "P_80"
        Uttaksgrad.HUNDRE_PROSENT -> "P_100"
    }

    // Updated to build periods with periodeFom/periodeTom
    private fun utenlandsperiode(periode: Opphold) = UtenlandsperiodeForSimuleringDto(
        land = periode.land.name,
        periodeFom = periode.fom,
        periodeTom = periode.tom
    )

    private fun alder(source: Alder) =
        SimulatorAlderSpec(source.aar, source.maaneder)
}
