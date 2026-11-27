package no.nav.pensjon.kalkulator.vedtak.api.v2

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(VedtakV2Controller::class)
@Import(MockSecurityConfiguration::class)
class VedtakV2ControllerTest : FunSpec() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var vedtakService: VedtakMedUtbetalingService

    @MockkBean(relaxed = true)
    private lateinit var traceAid: TraceAid

    @MockkBean
    private lateinit var pidExtractor: PidExtractor

    @MockkBean
    private lateinit var adresseService: FortroligAdresseService

    @MockkBean
    private lateinit var auditor: Auditor

    init {
        beforeSpec {
            every { traceAid.begin() } returns Unit
            every { pidExtractor.pid() } returns pid
            every { adresseService.adressebeskyttelseGradering(any()) } returns AdressebeskyttelseGradering.UGRADERT
            every { auditor.audit(any(), any()) } returns Unit
        }

        listOf(1234 to 2345, 0 to 0, null to null, null to 2345, 1234 to null).forEach { (afp, fremtidigAfp) ->
            test("flere vedtak med livsvarig offentlig AFP $afp og fremtidig AFP $fremtidigAfp") {
                coEvery { vedtakService.hentVedtakMedUtbetaling() } returns VedtakSamling(
                    loependeAlderspensjon = LoependeAlderspensjon(
                        grad = Uttaksgrad.TJUE_PROSENT,
                        fom = LocalDate.of(2020, 12, 1),
                        uttaksgradFom = LocalDate.of(2021, 1, 1),
                        utbetalingSisteMaaned = Utbetaling(
                            beloep = BigDecimal("100"),
                            posteringsdato = LocalDate.of(2026, 11, 20)
                        ),
                        sivilstatus = Sivilstatus.GIFT,
                        harGjenlevenderett = true,
                        harUtenlandsopphold = true,
                        livsvarigOffentligAfp = afp
                    ),
                    fremtidigAlderspensjon = FremtidigAlderspensjon(
                        grad = Uttaksgrad.FOERTI_PROSENT,
                        fom = LocalDate.of(2027, 1, 1),
                        sivilstatus = Sivilstatus.GIFT,
                        livsvarigOffentligAfp = fremtidigAfp
                    ),
                    ufoeretrygd = LoependeUfoeretrygd(grad = 50, fom = LocalDate.of(2021, 12, 1)),
                    privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 12, 1)),
                    tidsbegrensetOffentligAfp = LoependeEntitet(fom = LocalDate.of(2024, 2, 1)),
                    gjenlevenderett = Gjenlevenderett(
                        avdoedPid = pid,
                        avdoedNavn = Navn(fornavn = "F", mellomnavn = "M", etternavn = "E"),
                        doedsdato = LocalDate.of(2021, 2, 3),
                        foersteVirkningsdato = LocalDate.of(2022, 3, 1)
                    ),
                    avdoed = InformasjonOmAvdoed(
                        pid = pid,
                        doedsdato = LocalDate.of(2021, 2, 3),
                        foersteAlderspensjonVirkningsdato = LocalDate.of(2020, 1, 1),
                        aarligPensjonsgivendeInntektErMinst1G = true,
                        harTilstrekkeligMedlemskapIFolketrygden = false,
                        antallAarUtenlands = 3,
                        erFlyktning = true
                    )
                )

                val afpFelt = afp?.let { ""","livsvarigOffentligAfp":$it""" } ?: ""
                val fremtidigAfpFelt = fremtidigAfp?.let { ""","livsvarigOffentligAfp":$it""" } ?: ""
                val result = mvc.get(URL).asyncDispatch().andReturn()

                with(result.response) {
                    status shouldBe 200
                    contentAsString shouldBe
                            """{"harVedtak":true,"loependeAlderspensjon":{"grad":20,"fom":"2020-12-01","uttaksgradFom":"2021-01-01","sisteUtbetaling":{"beloep":100,"utbetalingsdato":"2026-11-20"},"sivilstatus":"GIFT","harGjenlevenderett":true,"harUtenlandsopphold":true$afpFelt},"fremtidigAlderspensjon":{"grad":40,"fom":"2027-01-01"$fremtidigAfpFelt},"ufoeretrygdgrad":50,"privatAfpFom":"2022-12-01","tidsbegrensetOffentligAfpFom":"2024-02-01","gjenlevenderett":{"avdoedPid":"${pid.value}","avdoedNavn":{"fornavn":"F","mellomnavn":"M","etternavn":"E"},"doedsdato":"2021-02-03","foersteVirkningsdato":"2022-03-01"},"avdoed":{"pid":"${pid.value}","doedsdato":"2021-02-03","foersteAlderspensjonVirkningsdato":"2020-01-01","aarligPensjonsgivendeInntektErMinst1G":true,"harTilstrekkeligMedlemskapIFolketrygden":false,"antallAarUtenlands":3,"erFlyktning":true}}"""
                }
            }
        }

        test("ingen vedtak") {
            coEvery { vedtakService.hentVedtakMedUtbetaling() } returns VedtakSamling(
                loependeAlderspensjon = null,
                fremtidigAlderspensjon = null,
                ufoeretrygd = null,
                privatAfp = null,
                avdoed = null
            )

            val result = mvc.get(URL).asyncDispatch().andReturn()

            with(result.response) {
                status shouldBe 200
                contentAsString shouldBe """{"harVedtak":false}"""
            }
        }

        test("bare fremtidig alderspensjon") {
            coEvery { vedtakService.hentVedtakMedUtbetaling() } returns VedtakSamling(
                loependeAlderspensjon = null,
                fremtidigAlderspensjon = FremtidigAlderspensjon(
                    grad = Uttaksgrad.HUNDRE_PROSENT,
                    fom = LocalDate.of(2027, 1, 1),
                    sivilstatus = Sivilstatus.UGIFT,
                    livsvarigOffentligAfp = 2345
                ),
                ufoeretrygd = null,
                privatAfp = null,
                avdoed = null
            )

            val result = mvc.get(URL).asyncDispatch().andReturn()

            with(result.response) {
                status shouldBe 200
                contentAsString shouldBe
                        """{"harVedtak":true,"fremtidigAlderspensjon":{"grad":100,"fom":"2027-01-01","livsvarigOffentligAfp":2345}}"""
            }
        }

        listOf(HttpStatus.SERVICE_UNAVAILABLE to 503, HttpStatus.BAD_REQUEST to 500).forEach { (penStatus, expectedStatus) ->
            test("gir $expectedStatus ved PEN-feil $penStatus") {
                coEvery { vedtakService.hentVedtakMedUtbetaling() } throws
                        EgressException("PEN-feil", statusCode = penStatus)

                mvc.get(URL).asyncDispatch().andReturn().response.status shouldBe expectedStatus
            }
        }
    }

    private companion object {
        private const val URL = "/api/v2/vedtak"
    }
}
