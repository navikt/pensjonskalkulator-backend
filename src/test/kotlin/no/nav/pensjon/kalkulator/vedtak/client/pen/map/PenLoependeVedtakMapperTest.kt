package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
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

    listOf(1234 to 2345, 0 to 0, null to null, null to 2345, 1234 to null).forEach { (afp, fremtidigAfp) ->
        should("map livsvarig offentlig AFP $afp og fremtidig AFP $fremtidigAfp") {
            val dto = PenLoependeVedtakDto(
                alderspensjon = PenGjeldendeVedtakApDto(
                    grad = 20,
                    fraOgMed = LocalDate.of(2026, 1, 1),
                    sivilstatus = "GIFT",
                    harGjenlevenderett = false,
                    harUtenlandsopphold = false,
                    afpOffentligLivsvarig = afp
                ),
                alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                    grad = 40,
                    fraOgMed = LocalDate.of(2027, 1, 1),
                    sivilstatus = "GIFT",
                    harGjenlevenderett = false,
                    harUtenlandsopphold = false,
                    afpOffentligLivsvarig = fremtidigAfp
                ),
                afpPrivat = null,
                afpOffentlig = null,
                ufoeretrygd = null,
                avdoed = null
            )

            with(PenLoependeVedtakMapper.fromDto(dto)) {
                loependeAlderspensjon!!.afpOffentligLivsvarig shouldBe afp
                fremtidigAlderspensjon!!.afpOffentligLivsvarig shouldBe fremtidigAfp
            }
        }
    }

    listOf(1234 to 2345, 0 to 0, null to null).forEach { (afp, fremtidigAfp) ->
        should("ignore fremtidig vedtak med samme grad ved AFP $afp til $fremtidigAfp") {
            val alderspensjon = PenGjeldendeVedtakApDto(
                grad = 100,
                fraOgMed = LocalDate.of(2026, 1, 1),
                sivilstatus = "GIFT",
                harGjenlevenderett = false,
                harUtenlandsopphold = false,
                afpOffentligLivsvarig = afp
            )
            val dto = PenLoependeVedtakDto(
                alderspensjon = alderspensjon,
                alderspensjonIFremtid = alderspensjon.copy(
                    fraOgMed = LocalDate.of(2027, 1, 1),
                    afpOffentligLivsvarig = fremtidigAfp
                ),
                afpPrivat = null,
                afpOffentlig = null,
                ufoeretrygd = null,
                avdoed = null
            )

            PenLoependeVedtakMapper.fromDto(dto).fremtidigAlderspensjon shouldBe null
        }
    }

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
                grad = 20,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB",
                harGjenlevenderett = true,
                harUtenlandsopphold = true
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 40,
                fraOgMed = LocalDate.of(2022, 2, 2),
                sivilstatus = "GIFT",
                harGjenlevenderett = false,
                harUtenlandsopphold = false
            ),
            ufoeretrygd = PenGjeldendeUfoeregradDto(grad = 2, fraOgMed = LocalDate.of(2021, 1, 2)),
            afpPrivat = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 12, 31)),
            afpOffentlig = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 6, 15)),
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1),
            avdoed = null
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result) {
            tidsbegrensetOffentligAfp?.fom shouldBe LocalDate.of(2021, 6, 15)
            privatAfp?.fom shouldBe LocalDate.of(2021, 12, 31)

            with(loependeAlderspensjon!!) {
                grad shouldBe Uttaksgrad.TJUE_PROSENT
                fom shouldBe LocalDate.of(2021, 1, 1)
                sivilstatus shouldBe Sivilstatus.SAMBOER
            }

            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2021, 1, 2)
            }

            with(fremtidigAlderspensjon!!) {
                grad shouldBe Uttaksgrad.FOERTI_PROSENT
                fom shouldBe LocalDate.of(2022, 2, 2)
                sivilstatus shouldBe Sivilstatus.GIFT
            }
        }
    }

    should("ignore fremtidig vedtak med samme grad") {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(
                grad = 20,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB",
                harGjenlevenderett = false,
                harUtenlandsopphold = false
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 40,
                fraOgMed = LocalDate.now().plusMonths(2),
                sivilstatus = "GIFT",
                harGjenlevenderett = true,
                harUtenlandsopphold = true
            ),
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null,
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1),
            avdoed = null
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result.loependeAlderspensjon!!) {
            grad shouldBe Uttaksgrad.TJUE_PROSENT
            fom shouldBe LocalDate.of(2021, 1, 1)
            sivilstatus shouldBe Sivilstatus.SAMBOER
        }
    }
})