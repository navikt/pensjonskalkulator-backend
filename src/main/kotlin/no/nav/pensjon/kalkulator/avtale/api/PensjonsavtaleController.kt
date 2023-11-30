package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toDto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.json.ObjectMapperConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PensjonsavtaleController(
    private val service: PensjonsavtaleService,
    private val traceAid: TraceAid,
    private val tempPidGetter: PidGetter? = null
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler",
        description = "Henter pensjonsavtalene til den innloggede brukeren. I request må verdi av 'maaneder' være 0..11."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av pensjonsavtaler utført. I respons er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av pensjonsavtaler kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun fetchAvtaler(@RequestBody spec: PensjonsavtaleIngressSpecDto): PensjonsavtalerDto {
        traceAid.begin()
        log.debug { "Request for pensjonsavtaler V1: $spec" }
        val mockFnr = tempPidGetter?.pid()?.value

        return try {
            if ("10836397849" == mockFnr) mockMangeAvtaler() else

            toDto(timed(service::fetchAvtaler, fromDto(spec), "pensjonsavtaler V1"))
                .also { log.debug { "Pensjonsavtaler respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av pensjonsavtaler"

        /**
         * Temporary function for testing many pensjonsavtaler
         */
        private fun mockMangeAvtaler(): PensjonsavtalerDto =
             ObjectMapperConfiguration().objectMapper().readValue(
                """{
  "avtaler": [
    {
      "produktbetegnelse": "Innskuddpensjon (2 perioder - gradert)",
      "kategori": "UNKNOWN",
      "startAar": 70,
      "sluttAar": 75,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 70, "maaneder": 0 },
          "sluttAlder": { "aar": 72, "maaneder": 11 },
          "aarligUtbetaling": 100000,
          "grad": 100
        },
        {
          "startAlder": { "aar": 73, "maaneder": 0 },
          "sluttAlder": { "aar": 74, "maaneder": 11 },
          "aarligUtbetaling": 50000,
          "grad": 100
        }
      ]
    },
    {
      "produktbetegnelse": "Nordea Liv",
      "kategori": "PRIVAT_TJENESTEPENSJON",
      "startAar": 75,
      "sluttAar": 75,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 75, "maaneder": 0 },
          "sluttAlder": { "aar": 75, "maaneder": 11 },
          "aarligUtbetaling": 12345,
          "grad": 100
        }
      ]
    },
    {
      "produktbetegnelse": "Gjensidige (Gjensidige avtale)",
      "kategori": "PRIVAT_TJENESTEPENSJON",
      "startAar": 77,
      "sluttAar": 87,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 77, "maaneder": 2 },
          "sluttAlder": { "aar": 87, "maaneder": 1 },
          "aarligUtbetaling": 12000,
          "grad": 100
        }
      ]
    },
    {
      "produktbetegnelse": "Oslo Pensjonsforsikring (livsvarig eksempel)",
      "kategori": "OFFENTLIG_TJENESTEPENSJON",
      "startAar": 62,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 62, "maaneder": 0 },
          "aarligUtbetaling": 23456,
          "grad": 100
        }
      ]
    },
    {
      "produktbetegnelse": "DNB",
      "kategori": "INDIVIDUELL_ORDNING",
      "startAar": 67,
      "sluttAar": 77,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 67, "maaneder": 0 },
          "sluttAlder": { "aar": 77, "maaneder": 11 },
          "aarligUtbetaling": 37264,
          "grad": 100
        }
      ]
    },
    {
      "produktbetegnelse": "IPS",
      "kategori": "INDIVIDUELL_ORDNING",
      "startAar": 70,
      "sluttAar": 75,
      "utbetalingsperioder": [
        {
          "startAlder": { "aar": 70, "maaneder": 6 },
          "sluttAlder": { "aar": 75, "maaneder": 5 },
          "aarligUtbetaling": 41802,
          "grad": 100
        }
      ]
    }
  ],
  "utilgjengeligeSelskap": []
}""", PensjonsavtalerDto::class.java
            )
    }
}
