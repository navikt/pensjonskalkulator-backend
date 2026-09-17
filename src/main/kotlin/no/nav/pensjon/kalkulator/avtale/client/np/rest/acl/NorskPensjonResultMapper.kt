package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Selskap
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonSluttAlderMapper.sluttAar
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR

object NorskPensjonResultMapper {

    /**
     *  Norsk Pensjon regner "til", vi regner "til og med" => forskyvning 1
     */
    const val SLUTTMAANED_FORSKYVNING = 1

    private const val DEFAULT_VALUE = "ukjent"

    fun fromDto(dto: NorskPensjonResult) =
        Pensjonsavtaler(
            avtaler = pensjonsavtaler(dto) ?: emptyOrFault(dto),
            utilgjengeligeSelskap = utilgjengeligeSelskap(dto) ?: emptyList()
        )

    private fun pensjonsavtaler(dto: NorskPensjonResult) =
        dto.pensjonsRettigheter?.map {
            Pensjonsavtale(
                avtalenummer = it.avtalenummer ?: "",
                arbeidsgiver = it.arbeidsgiver ?: DEFAULT_VALUE,
                selskapsnavn = it.selskapsnavn ?: DEFAULT_VALUE,
                produktbetegnelse = it.produktbetegnelse ?: DEFAULT_VALUE,
                kategori = Kategori.fromExternalValue(it.kategori).internalValue,
                underkategori = Underkategori.fromExternalValue(it.underkategori).internalValue,
                innskuddssaldo = it.innskuddssaldo ?: 0,
                naavaerendeAvtaltAarligInnskudd = it.naavaerendeAvtaltAarligInnskudd ?: 0,
                pensjonsbeholdningForventet = it.pensjonsbeholdningForventet ?: 0,
                pensjonsbeholdningNedreGrense = 0,
                pensjonsbeholdningOvreGrense = 0,
                avkastningsgaranti = it.avkastningsgaranti ?: false,
                beregningsmodell = Beregningsmodell.fromExternalValue(it.beregningsmodell).internalValue,
                startAar = it.startAlder ?: 0,
                sluttAar = sluttAar(it.sluttAlder, it.utbetalingsperioder),
                opplysningsdato = it.opplysningsdato ?: DEFAULT_VALUE,
                manglendeGraderingAarsak = AarsakManglendeGradering.fromExternalValue(it.aarsakManglendeGradering).internalValue,
                manglendeBeregningAarsak = AarsakIkkeBeregnet.internalValue(externalValue = it.aarsakIkkeBeregnet),
                utbetalingsperioder = it.utbetalingsperioder?.map(::utbetalingsperiode) ?: emptyList()
            )
        }

    private fun utilgjengeligeSelskap(dto: NorskPensjonResult) =
        dto.utilgjengeligeInnretninger?.map {
            Selskap(
                navn = it.selskapsnavn ?: DEFAULT_VALUE,
                heltUtilgjengelig = it.heltUtilgjengelig ?: false,
                antallManglendeRettigheter = it.antallManglendeRettigheter ?: 0,
                kategori = Kategori.fromExternalValue(it.kategori).internalValue,
                feilkode = it.feilkode ?: ""
            )
        }

    private fun utbetalingsperiode(source: UtbetalingsperiodeDto) =
        Utbetalingsperiode(
            startAlder = Alder(aar = source.startAlder, maaneder = source.startMaaned),
            sluttAlder = source.sluttAlder?.let { sluttalder(it, source.sluttMaaned!!) },
            aarligUtbetalingForventet = source.aarligUtbetalingForventet ?: 0,
            aarligUtbetalingNedreGrense = source.aarligUtbetalingNedreGrense ?: 0,
            aarligUtbetalingOvreGrense = source.aarligUtbetalingOvreGrense ?: 0,
            grad = source.grad.let { Uttaksgrad.from(it) }
        )

    private fun sluttalder(norskPensjonSluttAlder: Int, norskPensjonSluttMaaned: Int): Alder {
        val maaneder = norskPensjonSluttMaaned - SLUTTMAANED_FORSKYVNING

        return if (maaneder < 0)
            Alder(aar = norskPensjonSluttAlder - 1, maaneder = maaneder + MAANEDER_PER_AAR)
        else
            Alder(aar = norskPensjonSluttAlder, maaneder = maaneder)
    }

    private fun emptyOrFault(dto: NorskPensjonResult) =
        emptyList<Pensjonsavtale>()
}