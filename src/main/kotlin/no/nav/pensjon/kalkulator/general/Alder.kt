package no.nav.pensjon.kalkulator.general

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR
import java.time.LocalDate

/**
 * Alder i år og måneder.
 * Månedsverdi er 0 til 11 og betegner antall helt fylte måneder
 * (en alder av 62 år og 360 dager blir dermed "rundet av" nedover til 62 år og 11 måneder)
 */
data class Alder(@field:NotNull val aar: Int, @field:NotNull val maaneder: Int) {
    init {
        // Response form Norsk Pensjon violates this: require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }

    infix fun lessThan(other: Alder?): Boolean =
        other?.let { aar < it.aar || aar == it.aar && maaneder < it.maaneder } ?: true

    infix fun lessThanOrEqualTo(other: Alder?): Boolean =
        other?.let { aar < it.aar || aar == it.aar && maaneder <= it.maaneder } ?: true

    infix fun minusAar(antallAar: Int): Alder =
        normalisedAlder(aar = aar - antallAar, maaneder)

    infix fun plussMaaneder(antallMaaneder: Int): Alder =
        normalisedAlder(aar, maaneder = maaneder + antallMaaneder)

    companion object {

        fun from(foedselDato: LocalDate, dato: LocalDate): Alder {
            // NB: Hvis samme dayOfMonth regnes ikke måneden som helt fylt, dermed fratrekk 1
            val delmaanedFratrekk = if (dato.dayOfMonth - foedselDato.dayOfMonth <= 0) 1 else 0

            return normalisedAlder(
                aar = dato.year - foedselDato.year,
                maaneder = dato.monthValue - foedselDato.monthValue - delmaanedFratrekk
            )
        }

        /**
         * Normalised alder = alder with måneder within [0, 11]
         */
        private fun normalisedAlder(aar: Int, maaneder: Int): Alder =
            when (numberFlow(maaneder)) {
                NumberFlow.OVER -> Alder(
                    aar = aar + 1,
                    maaneder = maaneder - MAANEDER_PER_AAR
                )

                NumberFlow.UNDER -> Alder(
                    aar = aar - 1,
                    maaneder = maaneder + MAANEDER_PER_AAR
                )

                else -> Alder(aar, maaneder)
            }

        private fun numberFlow(maaneder: Int): NumberFlow =
            when {
                maaneder < 0 -> NumberFlow.UNDER
                maaneder >= MAANEDER_PER_AAR -> NumberFlow.OVER
                else -> NumberFlow.NEUTRAL
            }

        private enum class NumberFlow {
            UNDER,
            NEUTRAL,
            OVER
        }
    }
}
