package no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.http.HttpStatus

/**
 * Data transfer object (DTO) for resultatet av henting av tjenestekontor-enheter for en ansatt.
 */
@JsonInclude(NON_NULL)
data class AnsattEnhetV1Result(
    @field:Schema(description = "Liste over enheter (tjenestekontor)")
    @field:NotNull
    val enhetListe: List<AnsattEnhetV1Tjenestekontor>,

    @field:Schema(description = "Eventuelt problem som oppstod ved henting av enheter")
    val problem: AnsattEnhetV1Problem?
)

data class AnsattEnhetV1Tjenestekontor(
    @field:NotNull val id: String,
    @field:NotNull val navn: String
)

data class AnsattEnhetV1Problem(
    @field:NotNull val kode: AnsattEnhetV1ProblemType,
    @field:NotNull val beskrivelse: String
)

enum class AnsattEnhetV1ProblemType(
    val internalValue: ProblemType,
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) {
    ANSATT_IKKE_FUNNET(internalValue = ProblemType.PERSON_IKKE_FUNNET, httpStatus = HttpStatus.NOT_FOUND),
    ANNEN_KLIENTFEIL(internalValue = ProblemType.ANNEN_KLIENTFEIL),
    SERVERFEIL(internalValue = ProblemType.ANNEN_SERVERFEIL, httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
}