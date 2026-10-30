package no.nav.pensjon.kalkulator.simulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.Merknader
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.time.TodayProvider
import java.time.LocalDate

class SimuleringServiceTest : ShouldSpec({

    val pidGetter: PidGetter = mockk { every { pid() } returns pid }

    context("funksjonen 'simulerPersonligAlderspensjon'") {
        should("use specified inntekt and sivilstatus") {
            val incomingSpec = impersonalSimuleringSpec(
                sivilstatus = Sivilstatus.UOPPGITT // angitt sivilstatus
            )
            val simuleringClient = arrangeSimuleringClient(incomingSpec)
            val inntektService: InntektService = mockk()
            val personService: PersonService = arrangePerson(sivilstand = Sivilstand.SKILT)

            val service =
                SimuleringService(
                    simuleringClient,
                    inntektService,
                    personService,
                    pidGetter = pidGetter,
                    time = arrangeTime(),
                    serviceberegnetAfpService = mockk(),
                    merknadService = mockk()
                )

            val response = service.simulerPersonligAlderspensjon(incomingSpec)

            response.alderspensjonListe[0].beloep shouldBe PENSJONSBELOEP
            verify { inntektService wasNot Called }
            verify(exactly = 1) { personService.getPerson() } // for fødselsdato (ikke sivilstatus)
            verify {
                simuleringClient.simulerPersonligAlderspensjon(
                    impersonalSpec = any(),
                    personalSpec = match { it.sivilstatus == Sivilstatus.UOPPGITT }, // = angitt sivilstatus
                )
            }
        }

        should("obtain registrert inntekt and sivilstatus when not specified") {
            val incomingSpec = impersonalSimuleringSpec(
                forventetInntekt = null, // inntekt ikke angitt
                sivilstatus = null // sivilstatus ikke angitt
            )
            val simuleringClient = arrangeSimuleringClient(incomingSpec)
            val inntektService = arrangeInntekt()
            val personService = arrangePerson(sivilstand = Sivilstand.UOPPGITT) // registrert sivilstatus

            val service =
                SimuleringService(
                    simuleringClient,
                    inntektService,
                    personService,
                    pidGetter,
                    time = arrangeTime(),
                    serviceberegnetAfpService = mockk(),
                    merknadService = mockk()
                )

            val response = service.simulerPersonligAlderspensjon(incomingSpec)

            response.alderspensjonListe[0].beloep shouldBe PENSJONSBELOEP
            verify(exactly = 1) { inntektService.sistePensjonsgivendeInntekt() }
            verify(exactly = 2) { personService.getPerson() } // for sivilstatus og fødselsdato
            verify {
                simuleringClient.simulerPersonligAlderspensjon(
                    impersonalSpec = any(),
                    personalSpec = match { it.sivilstatus == Sivilstatus.UOPPGITT }, // = registrert sivilstatus
                )
            }
        }
    }

    context("funksjonen 'simulerPensjon'") {
        should("inkludere alderspensjon og merknader i resultatet") {
            val incomingSpec = impersonalSimuleringSpec()
            val simuleringClient = arrangeSimuleringClient(incomingSpec)
            val inntektService: InntektService = mockk()
            val personService: PersonService = arrangePerson(sivilstand = Sivilstand.UOPPGITT)

            val service =
                SimuleringService(
                    simuleringClient,
                    inntektService,
                    personService,
                    pidGetter = pidGetter,
                    time = arrangeTime(),
                    serviceberegnetAfpService = mockk(),
                    merknadService = mockk {
                        every { merknader() } returns Merknader(
                            perAar = mapOf(2021 to listOf(Merknad(kode = MerknadCode.AFP)))
                        )
                    }
                )

            val response = service.simulerPensjon(incomingSpec)

            with(response) {
                alderspensjonListe shouldHaveSize 1
                alderspensjonListe.first().alder shouldBe 67
                with(opptjeningListe[0]) {
                    merknadListe shouldHaveSize 1
                    merknadListe.first().kode shouldBe MerknadCode.AFP
                }
            }
        }
    }
})

private const val REGISTRERT_INNTEKT = 543210
private const val PENSJONSBELOEP = 123456

private val personalSpec = PersonalSimuleringSpec(
    pid,
    sivilstatus = Sivilstatus.UOPPGITT,
    aarligInntektFoerUttak = REGISTRERT_INNTEKT
)

private val inntekt = Inntekt(
    type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
    aar = 2023,
    beloep = REGISTRERT_INNTEKT.toBigDecimal()
)

private val simuleringResult =
    SimuleringResult(
        alderspensjonListe = listOf(
            SimulertAlderspensjon(
                alder = 67,
                beloep = PENSJONSBELOEP,
                inntektspensjonBeloep = 0,
                delingstall = 0.0,
                pensjonBeholdningFoerUttak = 0,
                sluttpoengtall = 0.0,
                poengaarFoer92 = 0,
                poengaarEtter91 = 0,
                forholdstall = 0.0,
                grunnpensjon = 0,
                tilleggspensjon = 0,
                pensjonstillegg = 0,
                skjermingstillegg = 0,
                kapittel19Pensjon = null,
                kapittel20Pensjon = null
            )
        ),
        alderspensjonMaanedsbeloep = AlderspensjonMaanedsbeloep(
            gradertUttak = null,
            heltUttak = 0
        ),
        livsvarigOffentligAfpListe = emptyList(),
        privatAfpListe = emptyList(),
        vilkaarsproeving = Vilkaarsproeving(innvilget = true, alternativ = null),
        harForLiteTrygdetid = false,
        trygdetid = 0,
        opptjeningListe = emptyList()
    )

private fun impersonalSimuleringSpec(forventetInntekt: Int? = REGISTRERT_INNTEKT, sivilstatus: Sivilstatus? = null) =
    ImpersonalSimuleringSpec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        sivilstatus = sivilstatus,
        eps = EpsSpec(levende = LevendeEps(harInntektOver2G = false, harPensjon = false)),
        forventetAarligInntektFoerUttak = forventetInntekt,
        heltUttak = HeltUttak(
            uttakFomAlder = Alder(aar = 67, maaneder = 1),
            inntekt = null
        ),
        utenlandsopphold = Utenlandsopphold(
            periodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
    )

private fun arrangeInntekt(): InntektService =
    mockk {
        every { sistePensjonsgivendeInntekt() } returns inntekt
    }

private fun arrangePerson(sivilstand: Sivilstand): PersonService =
    mockk {
        every { getPerson() } returns person(sivilstand)
    }

private fun arrangeSimuleringClient(incomingSpec: ImpersonalSimuleringSpec): SimuleringClient =
    mockk {
        every { simulerPersonligAlderspensjon(incomingSpec, personalSpec) } returns simuleringResult
    }

private fun arrangeTime(): TodayProvider =
    mockk {
        every { date() } returns LocalDate.of(2026, 1, 1)
    }