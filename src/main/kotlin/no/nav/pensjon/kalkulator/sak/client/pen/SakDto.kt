package no.nav.pensjon.kalkulator.sak.client.pen

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.sak.SakStatus
import no.nav.pensjon.kalkulator.sak.SakType
import org.springframework.util.StringUtils.hasLength

data class SakDto(val sakType: String, val sakStatus: String)

enum class PenSakType(val externalValue: String, val internalValue: SakType) {
    NONE("", SakType.NONE),
    UNKNOWN("?", SakType.UNKNOWN),
    GENERELL("GENRL", SakType.GENERELL),
    GJENLEVENDEYTELSE("GJENLEV", SakType.GJENLEVENDEYTELSE),
    OMSORGSOPPTJENING("OMSORG", SakType.OMSORGSOPPTJENING),
    UFOERETRYGD("UFOREP", SakType.UFOERETRYGD); // UFOREP = Uførepensjon

    companion object {
        private val values = PenSakType.values()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PEN sakstype '$externalValue'" } }
            else
                NONE
    }
}

enum class PenSakStatus(val externalValue: String, val internalValue: SakStatus) {
    NONE("", SakStatus.NONE),
    UNKNOWN("?", SakStatus.UNKNOWN),
    OPPRETTET("OPPRETTET", SakStatus.OPPRETTET),
    TIL_BEHANDLING("TIL_BEHANDLING", SakStatus.TIL_BEHANDLING),
    LOEPENDE("LOPENDE", SakStatus.LOEPENDE),
    AVSLUTTET("AVSLUTTET", SakStatus.AVSLUTTET);

    companion object {
        private val values = PenSakStatus.values()
        private val log = KotlinLogging.logger {}

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Unknown PEN sakstatus '$externalValue'" } }
            else
                NONE
    }
}
