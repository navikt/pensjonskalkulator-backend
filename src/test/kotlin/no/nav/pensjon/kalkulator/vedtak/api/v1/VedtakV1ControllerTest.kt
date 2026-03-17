package no.nav.pensjon.kalkulator.vedtak.api.v1

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.vedtak.*
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

@WebMvcTest(VedtakV1Controller::class)
@Import(MockSecurityConfiguration::class)
class VedtakV1ControllerTest : FunSpec() {

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

        test("flere vedtak") {
            coEvery {
                vedtakService.hentVedtakMedUtbetaling()
            } returns VedtakSamling(
                loependeAlderspensjon = LoependeAlderspensjon(
                    grad = 1,
                    fom = LocalDate.of(2020, 12, 1),
                    uttaksgradFom = LocalDate.of(2021, 1, 1),
                    sivilstatus = Sivilstatus.GIFT
                ),
                fremtidigAlderspensjon = FremtidigAlderspensjon(
                    grad = 10,
                    fom = LocalDate.of(2021, 12, 1),
                    sivilstatus = Sivilstatus.SKILT
                ),
                ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2021, 12, 1)),
                privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 12, 1))
            )

            val result = mvc.get(URL).asyncDispatch().andReturn()

            with(result.response) {
                status shouldBe 200
                contentAsString shouldBe FLERE_VEDTAK_JSON
            }
        }

        test("ingen vedtak") {
            coEvery {
                vedtakService.hentVedtakMedUtbetaling()
            } returns VedtakSamling(
                loependeAlderspensjon = null,
                fremtidigAlderspensjon = null,
                ufoeretrygd = null,
                privatAfp = null
            )

            val result = mvc.get(URL).asyncDispatch().andReturn()

            with(result.response) {
                status shouldBe 200
                contentAsString shouldBe INGEN_VEDTAK_JSON
            }
        }
    }

    private companion object {
        private const val URL = "/api/v1/vedtak"

        @Language("json")
        private const val INGEN_VEDTAK_JSON =
            """{"harVedtak":false}"""

        @Language("json")
        private const val FLERE_VEDTAK_JSON =
            """{"harVedtak":true,"loependeAlderspensjon":{"grad":1,"fom":"2020-12-01","uttaksgradFom":"2021-01-01","sivilstatus":"GIFT"},"fremtidigAlderspensjon":{"grad":10,"fom":"2021-12-01"},"ufoeretrygdgrad":2,"privatAfpFom":"2022-12-01"}"""
    }
}