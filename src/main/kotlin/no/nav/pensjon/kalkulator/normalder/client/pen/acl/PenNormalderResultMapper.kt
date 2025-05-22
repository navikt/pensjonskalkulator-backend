package no.nav.pensjon.kalkulator.normalder.client.pen.acl

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import kotlin.collections.map

object PenNormalderResultMapper {

    fun fromDto(source: PenNormalderResult): List<Aldersgrenser> =
        source.normertPensjonsalderListe?.map(::normalder) ?: throw exception(source)

    private fun normalder(source: PenNormertPensjonsalder) =
        Aldersgrenser(
            aarskull = source.aarskull,
            nedreAlder = Alder(source.nedreAar, source.nedreMaaned),
            normalder = Alder(source.aar, source.maaned),
            oevreAlder = Alder(source.oevreAar, source.oevreMaaned),
            verdiStatus = VerdiStatus.valueOf(source.type)
        )

    private fun exception(source: PenNormalderResult) =
        RuntimeException("Normalder-feil for årskull ${source.aarskull}: ${source.message}")
}
