package no.nav.pensjon.kalkulator.sak.client.pen

import no.nav.pensjon.kalkulator.sak.SakStatus
import no.nav.pensjon.kalkulator.sak.SakType
import org.springframework.util.StringUtils.hasLength

data class SakDto(val sakType: String, val sakStatus: String)

enum class PenSakType(val externalValue: String, val internalValue: SakType) {
    NONE("", SakType.NONE),
    UNKNOWN("?", SakType.UNKNOWN),
    GJENLEVENDEYTELSE("GJENLEV", SakType.GJENLEVENDEYTELSE),
    UFOEREPENSJON("UFOREP", SakType.UFOEREPENSJON);

    companion object {
        private val values = PenSakType.values()

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) = if (hasLength(externalValue)) UNKNOWN else NONE
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

        fun fromExternalValue(value: String?) =
            values.singleOrNull { it.externalValue.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?) = if (hasLength(externalValue)) UNKNOWN else NONE
    }
}
