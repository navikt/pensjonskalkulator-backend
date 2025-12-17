package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.map.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.SimulerOffentligTjenestepensjonFoer1963Dto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.SimuleringEtter2011Dto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.UtenlandsperiodeForSimuleringDto
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.SimulerTjenestepensjonFoer1963ResponseDto
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.Fnr
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.FremtidigInntektDto

object TpSimuleringFoer1963ClientMapper {

    fun fromDto(dto: SimulerTjenestepensjonFoer1963ResponseDto, foedselsdato: LocalDate) =
        dto.simulertPensjonListe.firstOrNull()?.let { sim ->
            OffentligTjenestepensjonSimuleringFoer1963Resultat(
                tpnr = sim.tpnr,
                navnOrdning = sim.navnOrdning,
                utbetalingsperioder = sim.utbetalingsperioder.map { periode ->
                    UtbetalingsperiodeResultat(
                        alderFom = Alder.from(
                            foedselsdato,
                            periode.datoFom.let(::epochMillisToLocalDate),
                        ),
                        alderTom = periode.datoTom?.let(::epochMillisToLocalDate)?.let { Alder.from(foedselsdato, it) },
                        grad = periode.grad,
                        arligUtbetaling = periode.arligUtbetaling,
                        ytelsekode = YtelseskodeFoer1963.fromExternalValue(periode.ytelsekode!!),
                        mangelfullSimuleringkode = periode.mangelfullSimuleringkode
                    )
                },
                feilkode = dto.feilkode?.let { Feilkode.fromExternalValue(it) }
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
            spec.heltUttak.inntekt?.tomAlder
                ?.let { SimulatorAnonymAlderDato(alder = alder(it), spec.foedselsdato).dato }
                ?: heltUttakFom


        return SimulerOffentligTjenestepensjonFoer1963Dto(
            SimuleringEtter2011Dto(
                simuleringType = SimulatorSimuleringType.fromInternalValue(spec.simuleringType).externalValue,
                fnr = Fnr(pid.value),
                sivilstatus = SimulatorSivilstand.fromInternalValue(spec.sivilstand).externalValue,
                eps2G = spec.eps.harInntektOver2G,
                epsPensjon = spec.eps.harPensjon,
                forventetInntekt = spec.forventetAarligInntektFoerUttak,
                inntektUnderGradertUttak = spec.gradertUttak?.aarligInntekt,
                inntektEtterHeltUttak = spec.heltUttak.inntekt?.aarligBeloep ?: 0,
                utenlandsperiodeForSimuleringList = spec.utenlandsopphold.periodeListe.map(::utenlandsperiode),
                afpInntektMndForUttak = spec.afpInntektMndForUttak,
                afpOrdning = if (spec.afpOrdning != null) SimulatorAfpOrdningType.fromInternalValue(spec.afpOrdning).externalValue else null,
                stillingsprosentOffHeltUttak = if (spec.stillingsprosentOffHeltUttak == "0") null else spec.stillingsprosentOffHeltUttak,
                stillingsprosentOffGradertUttak = spec.stillingsprosentOffGradertUttak,
                forsteUttakDato = gradertUttakFom ?: heltUttakFom,
                heltUttakDato = heltUttakFom,
                antallArInntektEtterHeltUttak = inntektTom.year - heltUttakFom.year,
                fnrAvdod = null,
                samtykke = true,
                utg = spec.gradertUttak?.grad?.prosentsats?.toString() ?: Uttaksgrad.HUNDRE_PROSENT.prosentsats.toString(),
                utenlandsopphold = spec.utenlandsopphold.antallAar ?: 0,
                flyktning = false,
                dodsdato = null,
                avdodAntallArIUtlandet = null,
                avdodInntektForDod = null,
                inntektAvdodOver1G = null,
                avdodMedlemAvFolketrygden = null,
                avdodFlyktning = null,
                simulerForTp = true,
                fremtidigInntektList = buildFremtidigInntektList(
                    forventetInntekt = spec.forventetAarligInntektFoerUttak,
                    forsteUttakDato = gradertUttakFom ?: heltUttakFom
                )
            )
        )
    }

    private fun buildFremtidigInntektList(
        forventetInntekt: Int?,
        forsteUttakDato: LocalDate
    ): List<FremtidigInntektDto> {
        if (forventetInntekt == null || forventetInntekt <= 0) {
            return emptyList()
        }

        val today = LocalDate.now()

        // Only create entries if first withdrawal is in the future
        return if (forsteUttakDato.isAfter(today)) {
            listOf(
                FremtidigInntektDto(
                    datoFom = today,
                    arliginntekt = forventetInntekt
                )
            )
        } else {
            emptyList()
        }
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
