package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

data class OpptjeningMerknader(
    val merknaderPerAar: Map<Int, List<String>>
)