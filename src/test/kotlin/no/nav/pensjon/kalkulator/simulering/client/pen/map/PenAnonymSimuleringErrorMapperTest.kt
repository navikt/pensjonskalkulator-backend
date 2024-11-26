package no.nav.pensjon.kalkulator.simulering.client.pen.map

import org.junit.jupiter.api.Assertions.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringError
import org.junit.jupiter.api.Test

class PenAnonymSimuleringErrorMapperTest {
    @Test
    fun `Dto should correctly be correctly mapped to a Internal Error Response when dto is of unknown format`() {
        val penDto = PenAnonymSimuleringError(feilmelding = "UNKNOWN ERROR")

        val simuleringError = PenAnonymSimuleringErrorMapper.fromDto(penDto)

        with(simuleringError) {
            assertEquals("PKU500InternalServerError", status)
        }
    }

    @Test
    fun `Dto should be correctly map a valid error message on a known format`() {
        val penDto = PenAnonymSimuleringError(feilmelding =
        "o.nav.domain.pensjon.kjerne.exception.PEN225AvslagVilkarsprovingForLavtTidligUttakException: Avslag på vilkårsprøving grunnet for lavt tidlig uttak."
        )

        val simuleringError = PenAnonymSimuleringErrorMapper.fromDto(penDto)

        with(simuleringError) {
            assertEquals("PKU225AvslagVilkarsprovingForLavtTidligUttakException", status)
            assertEquals("Avslag på vilkårsprøving grunnet for lavt tidlig uttak.", message)
        }
    }

    @Test
    fun `Dto should handle error message with only exception code`() {
        val penDto = PenAnonymSimuleringError(feilmelding = "PEN123Exception:")

        val simuleringError = PenAnonymSimuleringErrorMapper.fromDto(penDto)

        with(simuleringError) {
            assertEquals("PKU123Exception", status) // Extracted exception code
            assertEquals("", message) // No description, so empty message
        }
    }

}