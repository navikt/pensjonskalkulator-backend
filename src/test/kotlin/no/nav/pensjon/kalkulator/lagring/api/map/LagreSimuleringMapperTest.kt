package no.nav.pensjon.kalkulator.lagring.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.lagring.*
import no.nav.pensjon.kalkulator.lagring.api.dto.*

class LagreSimuleringMapperTest : ShouldSpec({

    should("map LagreSimuleringResponse to DTO") {
        LagreSimuleringMapperV1.toDto(
            LagreSimuleringResponse(
                brevId = "brev-123",
                sakId = "sak-456",
            )
        ) shouldBe LagreSimuleringResponseDtoV1(
            brevId = "brev-123",
            sakId = "sak-456",
            brevDevQ2Url = "https://pensjon-skribenten-web-q2.intern.dev.nav.no/saksnummer/sak-456/brev/brev-123",
        )
    }

    should("map all fields from DTO to domain object") {
        LagreSimuleringMapperV1.fromDto(
            LagreSimuleringSpecDtoV1(
                alderspensjonListe = listOf(
                    LagreAlderspensjonDto(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)
                ),
                livsvarigOffentligAfpListe = listOf(
                    LagreAldersbestemtUtbetalingDto(alderAar = 65, aarligBeloep = 50000, maanedligBeloep = 4167)
                ),
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = listOf(
                    LagrePrivatAfpDto(
                        alderAar = 62,
                        aarligBeloep = 30000,
                        kompensasjonstillegg = 1000,
                        kronetillegg = 2000,
                        livsvarig = 27000,
                        maanedligBeloep = 2500
                    )
                ),
                vilkaarsproevingsresultat = LagreVilkaarsproevingsresultatDto(
                    erInnvilget = true,
                    alternativ = LagreUttaksparametreDto(
                        gradertUttakAlder = LagreAlderDto(aar = 62, maaneder = 3),
                        uttaksgrad = 50,
                        heltUttakAlder = LagreAlderDto(aar = 67, maaneder = 0)
                    )
                ),
                trygdetid = LagreTrygdetidDto(antallAar = 40, erUtilstrekkelig = false),
                pensjonsgivendeInntektListe = listOf(
                    LagreAarligBeloepDto(aarstall = 2024, beloep = 600000)
                ),
                navEnhetId = null
            )
        ) shouldBe LagreSimulering(
            alderspensjonListe = listOf(
                LagreAlderspensjon(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)
            ),
            livsvarigOffentligAfpListe = listOf(
                LagreAfpOffentlig(alderAar = 65, aarligBeloep = 50000, maanedligBeloep = 4167)
            ),
            tidsbegrensetOffentligAfp = null,
            privatAfpListe = listOf(
                LagreAfpPrivat(
                    alderAar = 62,
                    aarligBeloep = 30000,
                    kompensasjonstillegg = 1000,
                    kronetillegg = 2000,
                    livsvarig = 27000,
                    maanedligBeloep = 2500
                )
            ),
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(
                erInnvilget = true,
                alternativ = LagreUttaksparametre(
                    gradertUttakAlder = LagreAlder(aar = 62, maaneder = 3),
                    uttaksgrad = 50,
                    heltUttakAlder = LagreAlder(aar = 67, maaneder = 0)
                )
            ),
            trygdetid = LagreTrygdetid(antallAar = 40, erUtilstrekkelig = false),
            pensjonsgivendeInntektListe = listOf(
                LagreAarligBeloep(aarstall = 2024, beloep = 600000)
            ),
            enhetsId = "4817"
        )
    }

    should("map with nullable fields as null") {
        LagreSimuleringMapperV1.fromDto(
            LagreSimuleringSpecDtoV1(
                alderspensjonListe = emptyList(),
                livsvarigOffentligAfpListe = null,
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = null,
                vilkaarsproevingsresultat = LagreVilkaarsproevingsresultatDto(
                    erInnvilget = false,
                    alternativ = null
                ),
                trygdetid = null,
                pensjonsgivendeInntektListe = null,
                navEnhetId = null
            )
        ) shouldBe LagreSimulering(
            alderspensjonListe = emptyList(),
            livsvarigOffentligAfpListe = emptyList(),
            tidsbegrensetOffentligAfp = null,
            privatAfpListe = emptyList(),
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(
                erInnvilget = false,
                alternativ = null
            ),
            trygdetid = null,
            pensjonsgivendeInntektListe = emptyList(),
            enhetsId = "4817"
        )
    }
})
