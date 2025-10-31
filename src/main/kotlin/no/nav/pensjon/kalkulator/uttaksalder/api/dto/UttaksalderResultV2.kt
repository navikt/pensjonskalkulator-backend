package no.nav.pensjon.kalkulator.uttaksalder.api.dto

import no.nav.pensjon.kalkulator.simulering.SimuleringStatus

data class UttaksalderResultV2(val aar: Int, val maaneder: Int)

data class UttaksalderError(val errorCode: SimuleringStatus, val cause: String?)