package no.nav.pensjon.kalkulator.lagring.client.sanity.map

import no.nav.pensjon.kalkulator.lagring.ForbeholdAvsnitt
import no.nav.pensjon.kalkulator.lagring.ForbeholdInnhold
import no.nav.pensjon.kalkulator.lagring.ForbeholdOgKortforbehold
import no.nav.pensjon.kalkulator.lagring.ForbeholdSeksjon
import no.nav.pensjon.kalkulator.lagring.Kortforbehold
import no.nav.pensjon.kalkulator.lagring.SanityVisningsvilkaar
import no.nav.pensjon.kalkulator.lagring.client.sanity.dto.SanityBlock
import no.nav.pensjon.kalkulator.lagring.client.sanity.dto.SanityForbeholdAvsnittDto
import no.nav.pensjon.kalkulator.lagring.client.sanity.dto.SanityForbeholdOgKortforbeholdResultDto
import no.nav.pensjon.kalkulator.lagring.client.sanity.dto.SanityKortforbeholdDto

object SanityForbeholdMapper {

    fun fromDto(documents: List<SanityForbeholdAvsnittDto>): ForbeholdInnhold =
        ForbeholdInnhold(
            seksjoner = documents.map(::mapToSeksjon)
        )

    fun fromKortforbeholdDto(document: SanityKortforbeholdDto): Kortforbehold =
        Kortforbehold(
            avsnitt = extractAvsnitt(document.innhold ?: emptyList())
        )

    fun fromForbeholdOgKortforbeholdDto(result: SanityForbeholdOgKortforbeholdResultDto): ForbeholdOgKortforbehold =
        ForbeholdOgKortforbehold(
            forbehold = result.forbehold?.takeIf { it.isNotEmpty() }?.let(::fromDto),
            kortforbehold = result.kortforbehold?.firstOrNull()?.let(::fromKortforbeholdDto)
        )

    private fun mapToSeksjon(source: SanityForbeholdAvsnittDto): ForbeholdSeksjon {
        val blocks = source.innhold ?: emptyList()
        val avsnitt = extractAvsnitt(blocks)

        return ForbeholdSeksjon(
            tittel = source.overskrift,
            avsnitt = avsnitt,
            vilkaarsliste = source.vilkaar
                ?.flatMap { it.betingelser.orEmpty() }
                ?.mapNotNull { SanityVisningsvilkaar.fromExternalValue(it.tag) }
                ?: emptyList()
        )
    }

    private fun extractAvsnitt(blocks: List<SanityBlock>): List<ForbeholdAvsnitt> {
        val result = mutableListOf<ForbeholdAvsnitt>()
        var i = 0

        while (i < blocks.size) {
            val block = blocks[i]
            val text = blockText(block)

            if (block.listItem == "bullet") {
                val bullets = mutableListOf<String>()
                while (i < blocks.size && blocks[i].listItem == "bullet") {
                    bullets.add(blockText(blocks[i]))
                    i++
                }
                if (result.isNotEmpty() && result.last().punktliste == null) {
                    val prev = result.removeLast()
                    result.add(ForbeholdAvsnitt(tekst = prev.tekst, punktliste = bullets))
                } else {
                    result.add(ForbeholdAvsnitt(tekst = "", punktliste = bullets))
                }
            } else {
                i++
                val bullets = mutableListOf<String>()
                while (i < blocks.size && blocks[i].listItem == "bullet") {
                    bullets.add(blockText(blocks[i]))
                    i++
                }
                result.add(
                    ForbeholdAvsnitt(
                        tekst = text,
                        punktliste = bullets.ifEmpty { null }
                    )
                )
            }
        }

        return result
    }

    private fun blockText(block: SanityBlock): String =
        block.children?.mapNotNull { it.text }?.joinToString("") ?: ""
}
