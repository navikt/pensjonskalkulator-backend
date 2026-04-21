package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.vedtak.InformasjonOmAvdoed
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenInformasjonOmAvdoedDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import java.time.LocalDate

class PenLoependeVedtakMapperTest : ShouldSpec({

    should("map informasjon om avdød") {
        val dto = PenLoependeVedtakDto(
            alderspensjon = null,
            alderspensjonIFremtid = null,
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null,
            gjeldendeUttaksgradFom = null,
            avdoed = PenInformasjonOmAvdoedDto(
                pid = pid.value,
                doedsdato = LocalDate.of(2025, 6, 14),
                foersteVirkningsdato = LocalDate.of(2021, 1, 1),
                aarligPensjonsgivendeInntektErMinst1G = true,
                harTilstrekkeligMedlemskapIFolketrygden = false,
                antallAarUtenlands = 3,
                erFlyktning = true
            )
        )

        PenLoependeVedtakMapper.fromDto(dto).avdoed shouldBe InformasjonOmAvdoed(
            pid = pid,
            doedsdato = LocalDate.of(2025, 6, 14),
            foersteAlderspensjonVirkningsdato = LocalDate.of(2021, 1, 1),
            aarligPensjonsgivendeInntektErMinst1G = true,
            harTilstrekkeligMedlemskapIFolketrygden = false,
            antallAarUtenlands = 3,
            erFlyktning = true
        )
    }

    should("ignore tidsbegrenset offentlig AFP") {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB"
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 3,
                fraOgMed = LocalDate.of(2022, 2, 2),
                sivilstatus = "GIFT"
            ),
            ufoeretrygd = PenGjeldendeUfoeregradDto(grad = 2, fraOgMed = LocalDate.of(2021, 1, 2)),
            afpPrivat = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 12, 31)),
            afpOffentlig = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 6, 15)),
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1),
            avdoed = null
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result) {
            pre2025OffentligAfp?.fom shouldBe LocalDate.of(2021, 6, 15)
            privatAfp?.fom shouldBe LocalDate.of(2021, 12, 31)

            with(loependeAlderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2021, 1, 1)
                sivilstatus shouldBe Sivilstatus.SAMBOER
            }

            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2021, 1, 2)
            }

            with(fremtidigAlderspensjon!!) {
                grad shouldBe 3
                fom shouldBe LocalDate.of(2022, 2, 2)
                sivilstatus shouldBe Sivilstatus.GIFT
            }
        }
    }

    should("ignore fremtidig vedtak med samme grad") {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB"
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.now().plusMonths(2),
                sivilstatus = "GIFT"
            ),
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null,
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1),
            avdoed = null
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result.loependeAlderspensjon!!) {
            grad shouldBe 1
            fom shouldBe LocalDate.of(2021, 1, 1)
            sivilstatus shouldBe Sivilstatus.SAMBOER
        }
    }
})
