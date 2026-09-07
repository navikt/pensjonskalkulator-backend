package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.NorskPensjonAlderDto
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad

object PensjonsavtaleFactory {

    val avtaleMedToUtbetalingsperioder =
        Pensjonsavtale(
            avtalenummer = "Avtale1",
            arbeidsgiver = "Firma1",
            selskapsnavn = "Selskap1",
            produktbetegnelse = "Produkt1",
            kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
            underkategori = AvtaleUnderkategori.FORENINGSKOLLEKTIV,
            innskuddssaldo = 1000,
            naavaerendeAvtaltAarligInnskudd = 100,
            pensjonsbeholdningForventet = 1000000,
            pensjonsbeholdningNedreGrense = 900000,
            pensjonsbeholdningOvreGrense = 1100000,
            avkastningsgaranti = false,
            beregningsmodell = EksternBeregningsmodell.BRANSJEAVTALE,
            startAar = 70,
            sluttAar = 80,
            opplysningsdato = "2023-01-01",
            manglendeGraderingAarsak = ManglendeEksternGraderingAarsak.IKKE_STOETTET,
            manglendeBeregningAarsak = ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE,
            utbetalingsperioder = listOf(
                utbetalingsperiodeMedSluttalder(),
                utbetalingsperiodeUtenSluttalder()
            )
        )

    fun pensjonsavtaler() = pensjonsavtaler(67)

    fun pensjonsavtalerV3(kategorier: List<AvtaleKategori>) =
        Pensjonsavtaler(kategorier.map(::pensjonsavtale), listOf(selskapV3()))

    fun pensjonsavtaler(startalder: Int) = Pensjonsavtaler(listOf(pensjonsavtale(startalder)), listOf(selskap()))

    // Avtaler med felter støttet i versjon 3 av tjenesten til Norsk Pensjon
    fun pensjonsavtalerV3() = Pensjonsavtaler(listOf(avtaleMedToUtbetalingsperioder), listOf(selskapV3()))

    private fun utbetalingsperiodeMedSluttalder() =
        Utbetalingsperiode(
            startAlder = NorskPensjonAlderDto(71, 1),
            sluttAlder = NorskPensjonAlderDto(81, 2),
            aarligUtbetaling = 10000,
            grad = Uttaksgrad.HUNDRE_PROSENT
        )

    private fun utbetalingsperiodeUtenSluttalder() =
        Utbetalingsperiode(
            startAlder = NorskPensjonAlderDto(72, 2),
            sluttAlder = null,
            aarligUtbetaling = 20000,
            grad = Uttaksgrad.AATTI_PROSENT
        )

    private fun pensjonsavtale(startalder: Int) =
        Pensjonsavtale(
            produktbetegnelse = "produkt1",
            kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
            startalder = startalder,
            sluttalder = 77,
            utbetalingsperioder = listOf(utbetalingsperiode())
        )

    private fun pensjonsavtale(kategori: AvtaleKategori) =
        Pensjonsavtale(
            produktbetegnelse = "produkt1",
            kategori = kategori,
            startalder = 67,
            sluttalder = 77,
            utbetalingsperioder = listOf(utbetalingsperiode())
        )

    private fun utbetalingsperiode() =
        Utbetalingsperiode(
            startAlder = NorskPensjonAlderDto(68, 2),
            sluttAlder = NorskPensjonAlderDto(78, 12),
            aarligUtbetaling = 123000,
            grad = Uttaksgrad.HUNDRE_PROSENT
        )

    private fun selskap() = Selskap("selskap1", true)

    // Selskap med felter støttet i versjon 3 av tjenesten til Norsk Pensjon
    private fun selskapV3() =
        Selskap(
            navn = "Selskap1",
            heltUtilgjengelig = true,
            antallManglendeRettigheter = 1,
            kategori = AvtaleKategori.FOLKETRYGD,
            feilkode = "Feil1"
        )
}
