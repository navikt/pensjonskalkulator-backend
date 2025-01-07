package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PersonligSimuleringExtendedResultMapperV8Test{
    @Test
    fun `extendedResultV8 maps domain to V8 DTO`() {
        PersonligSimuleringExtendedResultMapperV8.extendedResultV8(
            SimuleringResult(
                alderspensjon = listOf(
                    SimulertAlderspensjon(
                        alder = 67,
                        beloep = 123456,
                        inntektspensjonBeloep = 1,
                        garantipensjonBeloep = 2,
                        delingstall = 3.4,
                        pensjonBeholdningFoerUttak = 5
                    )
                ),
                alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(gradertUttak = 6, heltUttak = 7),
                afpPrivat = listOf(SimulertAfpPrivat(alder = 67, beloep = 12000)),
                afpOffentlig = listOf(SimulertAfpOffentlig(alder = 67, beloep = 12000)),
                vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
                harForLiteTrygdetid = true,
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                    SimulertOpptjeningGrunnlag(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
                )
            ), LocalDate.now().minusYears(67).minusMonths(1)
        ) shouldBe PersonligSimuleringResultV8(
            alderspensjon = listOf(
                PersonligSimuleringAlderspensjonResultV8(
                    alder = 67,
                    beloep = 123456,
                    inntektspensjonBeloep = 1,
                    garantipensjonBeloep = 2,
                    delingstall = 3.4,
                    pensjonBeholdningFoerUttakBeloep = 5
                )
            ),
            alderspensjonMaanedligVedEndring = PersonligSimuleringMaanedligPensjonResultV8(
                gradertUttakMaanedligBeloep = 6,
                heltUttakMaanedligBeloep = 7
            ),
            afpPrivat = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            afpOffentlig = listOf(PersonligSimuleringAarligPensjonResultV8(alder = 67, beloep = 12000)),
            vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(vilkaarErOppfylt = true, alternativ = null),
            harForLiteTrygdetid = true,
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                PersonligSimuleringAarligInntektResultV8(aar = 2001, pensjonsgivendeInntektBeloep = 501000),
                PersonligSimuleringAarligInntektResultV8(aar = 2002, pensjonsgivendeInntektBeloep = 502000)
            )
        )
    }
}