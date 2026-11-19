package no.nav.pensjon.kalkulator.lagring.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import no.nav.pensjon.kalkulator.lagring.api.dto.LagreAfpOffentligLivsvarigSimuleringDto
import no.nav.pensjon.kalkulator.lagring.api.dto.LagreSimuleringSpecDtoV1
import no.nav.pensjon.kalkulator.lagring.api.dto.LagreVilkaarsproevingsresultatDto
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.SimuleringBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.map.OpprettBrevDtoV1Mapper
import tools.jackson.databind.json.JsonMapper

class LivsvarigOffentligAfpBrevTest : ShouldSpec({
    val mapper = JsonMapper.builder().findAndAddModules().build()
    val beskrivelse = "AFP-beløpet er hentet fra <Statens pensjonskasse>."

    listOf(
        "bare beskrivelse" to """{"beskrivelse":"$beskrivelse"}""",
        "eksplisitt null i uttaksfeltene" to
                """{"vedGradertUttak":null,"vedHeltUttak":null,"beskrivelse":"$beskrivelse"}""",
        "null kroner" to """
            {
                "vedHeltUttak":{"alderAar":65,"aarligBeloep":0,"maanedligBeloep":0},
                "beskrivelse":"$beskrivelse"
            }
        """,
        "gradert og helt uttak" to """
            {
                "vedGradertUttak":{"alderAar":62,"aarligBeloep":100164,"maanedligBeloep":8347},
                "vedHeltUttak":{"alderAar":65,"aarligBeloep":100164,"maanedligBeloep":8347},
                "beskrivelse":"$beskrivelse"
            }
        """,
        "eksisterende klient uten beskrivelse" to """
            {
                "vedGradertUttak":null,
                "vedHeltUttak":{"alderAar":65,"aarligBeloep":100164,"maanedligBeloep":8347}
            }
        """,
    ).forEach { (scenario, json) ->
        should("videresende livsvarig AFP uendret med $scenario") {
            val afp = mapper.readValue(json, LagreAfpOffentligLivsvarigSimuleringDto::class.java)
            val spec = LagreSimuleringSpecDtoV1(
                alderspensjonListe = emptyList(),
                afpPrivat = null,
                afpOffentligLivsvarig = afp,
                afpOffentligTidsbegrenset = null,
                vilkaarsproevingsresultat = LagreVilkaarsproevingsresultatDto(true, null),
                trygdetid = null,
                pensjonsgivendeInntektListe = null,
                aarligInntektOgPensjonListe = null,
                pensjonsopptjeningListe = null,
                simuleringsinformasjon = null,
                maanedligAlderspensjonForKnekkpunkter = null,
                navEnhetId = null,
            )

            val request = OpprettBrevDtoV1Mapper.toDto(
                source = LagreSimuleringMapperV1.fromDto(spec),
                saksId = 42L,
                forbehold = null,
                kortforbehold = null,
            )
            val brevdata = request.statiskFagsystemBrevdata.shouldBeInstanceOf<SimuleringBrevDtoV1>()
            val videresendt = brevdata.simulering.afpOffentligLivsvarig
            mapper.readTree(mapper.writeValueAsString(videresendt)) shouldBe
                    mapper.readTree(mapper.writeValueAsString(afp))
        }
    }
})
