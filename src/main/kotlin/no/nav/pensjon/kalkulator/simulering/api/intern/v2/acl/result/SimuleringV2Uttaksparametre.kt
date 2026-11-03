package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.simulering.Alternativ

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SimuleringV2Uttaksparametre(
    @field:Schema(description = "Alder ved start av gradert uttak")
    val gradertUttakAlder: SimuleringV2Alder?,

    @field:Schema(description = "Uttaksgrad i prosent (manglende verdi betyr 100 %)")
    val uttaksgrad: Int?, // null implies 100 %

    @field:Schema(description = "Alder ved start av helt uttak")
    @field:NotNull
    val heltUttakAlder: SimuleringV2Alder
) {
    companion object {
        fun transferable(source: Alternativ) =
            SimuleringV2Uttaksparametre(
                gradertUttakAlder = source.gradertUttakAlder?.let(SimuleringV2Alder::transferable),
                uttaksgrad = transferable(source.uttakGrad),
                heltUttakAlder = SimuleringV2Alder.transferable(source.heltUttakAlder)
            )

        private fun transferable(grad: Uttaksgrad?): Int? =
            grad?.let {
                if (it == Uttaksgrad.HUNDRE_PROSENT) null else it.prosentsats
            }
    }
}