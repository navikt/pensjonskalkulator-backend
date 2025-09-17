package no.nav.pensjon.kalkulator.ekskludering.api.dto

//TODO: Should not mix version numbers (ApotekerStatusV1 vs EkskluderingAarsakV2)
data class ApotekerStatusV1(val apoteker: Boolean, val aarsak: EkskluderingAarsakV2)