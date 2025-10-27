package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.map

import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.dto.AfpStatus
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.dto.TpAfpOffentligLivsvarigDto

/**
 * Mapper for AFP Offentlig Livsvarig data.
 */
object TpAfpOffentligLivsvarigMapper {

    fun fromDto(response: TpAfpOffentligLivsvarigDto?): AfpOffentligLivsvarigResult {
        if (response == null) {
            return AfpOffentligLivsvarigResult(null, null)
        }

        // Map AFP status to boolean (true if INNVILGET)
        val afpStatus = when (response.statusAfp) {
            AfpStatus.INNVILGET -> true
            AfpStatus.UKJENT, AfpStatus.IKKE_SOKT, AfpStatus.SOKT, AfpStatus.AVSLAG -> false
        }

        val beloep = response.belopsListe.firstOrNull()?.belop

        return AfpOffentligLivsvarigResult(afpStatus, beloep)
    }
}
