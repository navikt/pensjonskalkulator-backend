package no.nav.pensjon.kalkulator.lagring.client.sanity.dto

data class SanityForbeholdOgKortforbeholdResponseDto(
    val result: SanityForbeholdOgKortforbeholdResultDto?
)

data class SanityForbeholdOgKortforbeholdResultDto(
    val forbehold: List<SanityForbeholdAvsnittDto>?,
    val kortforbehold: List<SanityKortforbeholdDto>?
)

data class SanityKortforbeholdDto(
    val innhold: List<SanityBlock>?
)

data class SanityForbeholdAvsnittDto(
    val overskrift: String?,
    val innhold: List<SanityBlock>?,
    val alltidSynlig: Boolean?,
    val vilkaar: List<SanityVilkaarGruppe>?
)

data class SanityBlock(
    val _type: String?,
    val children: List<SanitySpan>?,
    val style: String?,
    val listItem: String?,
    val level: Int?
)

data class SanitySpan(
    val _type: String?,
    val text: String?
)

data class SanityVilkaarGruppe(
    val betingelser: List<SanityBetingelse>?
)

data class SanityBetingelse(
    val tag: String?,
    val negert: Boolean?
)
