package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOFTPSpec
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV2 {
    private val log = KotlinLogging.logger {}

    fun fromDto(spec: IngressSimuleringOFTPSpecV2) : SimuleringOFTPSpec {
        return SimuleringOFTPSpec(
            foedselsdato = spec.foedselsdato,
            uttaksdato = uttakDato(spec.foedselsdato, spec.uttaksalder),
            sisteInntekt = spec.aarligInntektFoerUttakBeloep,
            aarIUtlandetEtter16 = antallAar(spec.utenlandsperiodeListe),
            brukerBaOmAfp = spec.brukerBaOmAfp,
            epsPensjon = spec.epsHarPensjon,
            eps2G = spec.epsHarInntektOver2G
        )
    }

    fun antallAar(oppholdListe: List<UtenlandsoppholdV2>): Int {
        val antallDager = antallDager(oppholdListe)
        val res = (antallDager / DAGER_PER_AAR).toInt()
        log.info { "( $antallDager / $DAGER_PER_AAR ).toInt()=$res" }
        return res
    }

    fun antallDager(oppholdListe: List<UtenlandsoppholdV2>): Long {
        val res = oppholdListe.sumOf { (it.tom ?: LocalDate.now()).toEpochDay() + 1 - it.fom.toEpochDay() } // +1 for å inkludere siste dagen
        log.info { "antall dager i $oppholdListe: $res" }
        return res
    }

    private const val DAGER_PER_AAR = 365
}