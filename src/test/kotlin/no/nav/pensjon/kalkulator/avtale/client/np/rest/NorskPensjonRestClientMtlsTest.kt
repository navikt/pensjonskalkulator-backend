package no.nav.pensjon.kalkulator.avtale.client.np.rest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.ssl.Pkcs12KeyStoreLoader
import no.nav.pensjon.kalkulator.tech.ssl.TestCertificates
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.springframework.boot.ssl.DefaultSslBundleRegistry
import org.springframework.boot.ssl.SslBundle
import org.springframework.boot.ssl.SslStoreBundle
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class NorskPensjonRestClientMtlsTest : FunSpec({
    val certificateAuthority = TestCertificates.certificateAuthority
    val clientCertificate = TestCertificates.clientCertificate()
    val serverCertificate = HeldCertificate.Builder()
        .commonName("localhost")
        .addSubjectAlternativeName("localhost")
        .signedBy(certificateAuthority)
        .build()
    val serverCertificates = HandshakeCertificates.Builder()
        .heldCertificate(serverCertificate, certificateAuthority.certificate)
        .addTrustedCertificate(certificateAuthority.certificate)
        .build()
    val trustStore = TestCertificates.trustStore(certificateAuthority.certificate)
    val password = "password".toCharArray()
    val clientAlias = "nav integrasjon norsk pensjon"

    lateinit var server: MockWebServer

    fun client(bundle: SslBundle): NorskPensjonRestClient {
        val bundles = DefaultSslBundleRegistry("norsk-pensjon", bundle)
        val traceAid = mockk<TraceAid> {
            every { callId() } returns "correlation-id"
        }
        val webClientConfig = WebClientConfig()
        val webClientBuilder = WebClient.builder().also(webClientConfig::customize)
        return NorskPensjonRestClient(
            baseUrl = server.url("/").toString().removeSuffix("/"),
            webClientBuilder = webClientBuilder,
            sslBundles = bundles,
            webClientConfig = webClientConfig,
            cacheManager = CaffeineCacheManager(),
            traceAid = traceAid,
            retryAttempts = "0"
        )
    }

    beforeTest {
        server = MockWebServer().apply {
            useHttps(serverCertificates.sslSocketFactory(), false)
            requireClientAuth()
            start()
        }
    }

    afterTest {
        server.shutdown()
    }

    test("uses client certificate and preserves Norsk Pensjon request contract") {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""{"pensjonsRettigheter":[],"utilgjengeligeInnretninger":[]}""")
        )
        val bundle = SslBundle.of(
            SslStoreBundle.of(
                Pkcs12KeyStoreLoader().load(
                    TestCertificates.pkcs12(clientCertificate, password, clientAlias),
                    password,
                    clientAlias
                ),
                String(password),
                trustStore
            ),
            org.springframework.boot.ssl.SslBundleKey.of(String(password), clientAlias)
        )

        client(bundle).fetchAvtaler(
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = 500_000,
                uttaksperioder = listOf(
                    UttaksperiodeSpec(Alder(67, 0), Uttaksgrad.HUNDRE_PROSENT, null)
                )
            ),
            Pid("01017012345")
        )

        val request = server.takeRequest()
        request.handshake?.peerPrincipal?.name shouldContain "Norsk Pensjon test client"
        request.getHeader("Organization-Number") shouldContain "889640782"
        request.getHeader("Correlation-Id") shouldContain "correlation-id"
        request.body.readUtf8() shouldContain """"foedselsnummer":"01017012345""""
    }

    test("TLS server rejects a request without client certificate") {
        server.enqueue(MockResponse().setBody("{}"))
        val bundleWithoutClientCertificate = SslBundle.of(
            SslStoreBundle.of(null, null, trustStore)
        )

        shouldThrowAny {
            WebClient.builder()
                .clientConnector(WebClientConfig().clientConnector(bundleWithoutClientCertificate))
                .build()
                .post()
                .uri(server.url("/").toString())
                .retrieve()
                .toBodilessEntity()
                .block()
        }
    }
})
