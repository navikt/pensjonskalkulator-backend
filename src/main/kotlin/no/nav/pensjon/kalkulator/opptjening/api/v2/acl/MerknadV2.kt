package no.nav.pensjon.kalkulator.opptjening.api.v2.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.merknad.Merknad

@JsonInclude(NON_NULL)
data class MerknadV2(
    @field:Schema(description = "Enumerert kode for merknadstype")
    @field:NotNull
    val kode: MerknadCodeV2,

    @field:Schema(description = "Informasjon om uttaksgrad – kun angitt for kode GRADERT_UTTAK, HELT_UTTAK og UTTAK_STOPPET")
    @field:NotNull
    val uttaksgrad: UttaksgradV2?
) {
    companion object {
        /**
         * Maps from domain representation to transferable representation (data transfer object).
         */
        fun transferable(source: Merknad) =
            MerknadV2(
                kode = MerknadCodeV2.transferable(source.kode),
                uttaksgrad = source.uttaksgrad?.let(UttaksgradV2::transferable)
            )
    }
}