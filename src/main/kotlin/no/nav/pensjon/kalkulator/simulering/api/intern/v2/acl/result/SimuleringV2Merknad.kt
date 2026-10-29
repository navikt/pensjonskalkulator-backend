package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.merknad.Merknad

@JsonInclude(NON_NULL)
data class SimuleringV2Merknad(
    @field:Schema(description = "Opplistet kode for merknadstype")
    @field:NotNull
    val kode: SimuleringV2Merknadkode,

    @field:Schema(description = "Informasjon om uttaksgrad – kun angitt for kode GRADERT_UTTAK, HELT_UTTAK og UTTAK_STOPPET")
    val uttaksgrad: SimuleringV2Uttaksgrad? = null
) {
    companion object {
        /**
         * Maps from domain representation to transferable representation (data transfer object).
         */
        fun transferable(source: Merknad) =
            SimuleringV2Merknad(
                kode = SimuleringV2Merknadkode.transferable(source.kode),
                uttaksgrad = source.uttaksgrad?.let(SimuleringV2Uttaksgrad::transferable)
            )
    }
}