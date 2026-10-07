package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Selskap
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.client.np.rest.acl.NorskPensjonSluttAlderMapper.sluttAar
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import java.time.LocalDate

object NorskPensjonResultMapper {

    private const val DEFAULT_VALUE = "ukjent"
    private val log = KotlinLogging.logger {}

    fun fromDto(dto: NorskPensjonResult, foedselsdato: LocalDate) =
        Pensjonsavtaler(
            avtaler = pensjonsavtaler(dto, foedselsdato) ?: emptyOrFault(),
            utilgjengeligeSelskap = utilgjengeligeSelskap(dto) ?: emptyList()
        )

    private fun pensjonsavtaler(dto: NorskPensjonResult, foedselsdato: LocalDate) =
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
                sluttAar = sluttAar(it.sluttAlder, it.utbetalingsperioder.orEmpty(), foedselsdato),
                opplysningsdato = it.opplysningsdato ?: DEFAULT_VALUE,
                manglendeGraderingAarsak = AarsakManglendeGradering.fromExternalValue(it.aarsakManglendeGradering).internalValue,
                manglendeBeregningAarsak = AarsakIkkeBeregnet.internalValue(externalValue = it.aarsakIkkeBeregnet),
                utbetalingsperioder = it.utbetalingsperioder.orEmpty().map { utbetalingsperiode(it, foedselsdato) }
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

    private fun utbetalingsperiode(source: UtbetalingsperiodeDto, foedselsdato: LocalDate) =
        Utbetalingsperiode(
            startAlder = source.datoFom?.let { Alder.from(foedselsdato, it)}
                ?: throw IllegalArgumentException("UtbetalingsperiodeDto mangler datoFom"),
            sluttAlder = source.datoTom?.let { Alder.from(foedselsdato, it)
                .also { log.warn { "Norsk pensjon REST: Utledet $it fra ${source.datoTom}" } }},
            aarligUtbetalingForventet = source.aarligUtbetalingForventet ?: 0,
            grad = source.grad.let { Uttaksgrad.from(it) }
        )

    private fun emptyOrFault() =
        emptyList<Pensjonsavtale>()
}