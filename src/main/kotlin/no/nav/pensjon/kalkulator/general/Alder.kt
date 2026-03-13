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
        Alder(aar = aar - antallAar, maaneder)

    infix fun minusMaaneder(antall: Int): Alder {
        if (antall < 0) return this plussMaaneder -antall

        val alderMaaneder = maaneder - antall % MAANEDER_PER_AAR
        val alderAar = aar - antall / MAANEDER_PER_AAR

        return if (alderMaaneder < 0)
            Alder(aar = alderAar - 1, maaneder = alderMaaneder + MAANEDER_PER_AAR)
        else
            Alder(aar = alderAar, maaneder = alderMaaneder)
    }

    infix fun plussMaaneder(antall: Int): Alder {
        if (antall < 0) return this minusMaaneder -antall

        val alderMaaneder = maaneder + antall % MAANEDER_PER_AAR
        val alderAar = aar + antall / MAANEDER_PER_AAR

        return if (alderMaaneder >= MAANEDER_PER_AAR)
            Alder(aar = alderAar + 1, maaneder = alderMaaneder - MAANEDER_PER_AAR)
        else
            Alder(aar = alderAar, maaneder = alderMaaneder)
    }

    companion object {

        /**
         * Beregner alder ved angitt dato.
         * Kun helt fylte år og helt fylte måneder telles med.
         * (Eksempel: En alder av 5 år, 11 måneder og 27 dager returneres som 5 år og 11 måneder.)
         * Bakgrunnen for dette er at det i pensjonssammenheng opereres med hele måneder;
         * det er den første dag i påfølgende måned som legges til grunn ved f.eks. uttak av pensjon.
         * -------------
         * NB: Fødselstidspunkt antas å være kl 12 på fødselsdatoen, mens tidspunktet på dato antas å være kl 00;
         * dermed vil det mangle en halv dag for å helt fylle en måned dersom dagverdien (dayOfMonth) er den samme.
         */
        fun from(foedselDato: LocalDate, dato: LocalDate): Alder {
            val delmaanedFratrekk = if (dato.dayOfMonth - foedselDato.dayOfMonth <= 0) 1 else 0
            // NB: Samme dayOfMonth anses som ufullstending måned og dermed fratrekk

            return normalisedAlder(
                aar = dato.year - foedselDato.year,
                maaneder = dato.monthValue - foedselDato.monthValue - delmaanedFratrekk
            )
        }

        /**
         * Normalised alder = alder with måneder within [0, 11].
         * NB: Not to be confused with "normert alder".
         */
        private fun normalisedAlder(aar: Int, maaneder: Int): Alder =
            when {
                maaneder < 0 -> Alder(
                    aar = aar - 1,
                    maaneder = maaneder + MAANEDER_PER_AAR
                )

                maaneder >= MAANEDER_PER_AAR -> Alder(
                    aar = aar + 1,
                    maaneder = maaneder - MAANEDER_PER_AAR
                )

                else -> Alder(aar, maaneder)
            }
    }
}
