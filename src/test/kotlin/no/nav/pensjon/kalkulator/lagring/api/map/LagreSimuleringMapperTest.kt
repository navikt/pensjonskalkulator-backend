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
                afpPrivat = LagreAfpPrivatSimuleringDto(
                    vedGradertUttak = null,
                    vedHeltUttak = LagrePrivatAfpDto(
                        alderAar = 62,
                        aarligBeloep = 30000,
                        kompensasjonstillegg = 1000,
                        kronetillegg = 2000,
                        livsvarig = 27000,
                        maanedligBeloep = 2500
                    )
                ),
                afpOffentligLivsvarig = LagreAfpOffentligLivsvarigSimuleringDto(
                    vedGradertUttak = null,
                    vedHeltUttak = LagreLivsvarigOffentligAfpDto(alderAar = 65, aarligBeloep = 50000, maanedligBeloep = 4167)
                ),
                afpOffentligTidsbegrenset = null,
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
                simuleringsinformasjon = LagreSimuleringsinformasjonDto(
                    gradertUttaksalder = null,
                    heltUttaksalder = LagreAlderDto(aar = 67, maaneder = 0),
                    sivilstatus = null,
                    utenlandsperioder = null,
                    kull = Kull.KAP20,
                    normertPensjonsalderPlassering = NormertPensjonsalderPlassering.ETTER_HELT
                ),
                maanedligAlderspensjonForKnekkpunkter = null,
                navEnhetId = null
            )
        ) shouldBe LagreSimulering(
            alderspensjonListe = listOf(
                LagreAlderspensjon(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)
            ),
            afpPrivat = LagreAfpPrivatSimulering(
                vedGradertUttak = null,
                vedHeltUttak = LagreAfpPrivat(
                    alderAar = 62,
                    aarligBeloep = 30000,
                    kompensasjonstillegg = 1000,
                    kronetillegg = 2000,
                    livsvarig = 27000,
                    maanedligBeloep = 2500
                )
            ),
            afpOffentligLivsvarig = LagreAfpOffentligLivsvarigSimulering(
                vedGradertUttak = null,
                vedHeltUttak = LagreLivsvarigOffentligAfp(alderAar = 65, aarligBeloep = 50000, maanedligBeloep = 4167)
            ),
            afpOffentligTidsbegrenset = null,
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
            simuleringsinformasjon = LagreSimuleringsinformasjon(
                gradertUttaksalder = null,
                heltUttaksalder = LagreAlder(aar = 67, maaneder = 0),
                sivilstatus = null,
                utenlandsperioder = null,
                kull = Kull.KAP20,
                normertPensjonsalderPlassering = NormertPensjonsalderPlassering.ETTER_HELT
            ),
            maanedligAlderspensjonForKnekkpunkter = null,
            enhetsId = "4817"
        )
    }

    should("map with nullable fields as null") {
        LagreSimuleringMapperV1.fromDto(
            LagreSimuleringSpecDtoV1(
                alderspensjonListe = emptyList(),
                afpPrivat = null,
                afpOffentligLivsvarig = null,
                afpOffentligTidsbegrenset = null,
                vilkaarsproevingsresultat = LagreVilkaarsproevingsresultatDto(
                    erInnvilget = false,
                    alternativ = null
                ),
                trygdetid = null,
                pensjonsgivendeInntektListe = null,
                simuleringsinformasjon = null,
                maanedligAlderspensjonForKnekkpunkter = null,
                navEnhetId = null
            )
        ) shouldBe LagreSimulering(
            alderspensjonListe = emptyList(),
            afpPrivat = null,
            afpOffentligLivsvarig = null,
            afpOffentligTidsbegrenset = null,
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(
                erInnvilget = false,
                alternativ = null
            ),
            trygdetid = null,
            pensjonsgivendeInntektListe = null,
            simuleringsinformasjon = null,
            maanedligAlderspensjonForKnekkpunkter = null,
            enhetsId = "4817"
        )
    }
})
