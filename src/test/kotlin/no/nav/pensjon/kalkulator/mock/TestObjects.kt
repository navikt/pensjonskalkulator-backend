package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.Garantipensjon
import no.nav.pensjon.kalkulator.simulering.Kapittel19Pensjon
import no.nav.pensjon.kalkulator.simulering.Kapittel20Pensjon
import no.nav.pensjon.kalkulator.simulering.SimulertAlderspensjon
import org.springframework.security.oauth2.jwt.Jwt

object TestObjects {
    val jwt = Jwt("j.w.t", null, null, mapOf("k" to "v"), mapOf("k" to "v"))

    val pid1 = Pid("22925399748")

    fun alderspensjon(gjenlevendetillegg: Int = 15, alderAar: Int = 67, beloep: Int = 123456) =
        SimulertAlderspensjon(
            alder = alderAar,
            beloep,
            inntektspensjonBeloep = 1,
            delingstall = 3.4,
            pensjonBeholdningFoerUttak = 5,
            sluttpoengtall = 5.11,
            poengaarFoer92 = 13,
            poengaarEtter91 = 27,
            forholdstall = 0.971,
            grunnpensjon = 55810,
            tilleggspensjon = 134641,
            pensjonstillegg = -70243,
            skjermingstillegg = 14,
            kapittel19Pensjon = Kapittel19Pensjon(
                andelsbroek = 0.6,
                trygdetidAntallAar = 40,
                basispensjon = 100,
                restpensjon = 101,
                gjenlevendetillegg,
                minstePensjonsnivaaSats = 1.23
            ),
            kapittel20Pensjon = Kapittel20Pensjon(
                andelsbroek = 0.4,
                trygdetidAntallAar = 39,
                garantipensjon = Garantipensjon(aarligBeloep = 2, sats = 2.34),
                garantitillegg = 201
            )
        )
}
