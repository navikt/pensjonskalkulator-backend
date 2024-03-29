package no.nav.pensjon.kalkulator.general

enum class Uttaksgrad(val prosentsats: Int) {
    NULL(0),
    TJUE_PROSENT(20),
    FOERTI_PROSENT(40),
    FEMTI_PROSENT(50),
    SEKSTI_PROSENT(60),
    AATTI_PROSENT(80),
    HUNDRE_PROSENT(100);

    companion object {
        private val values = entries.toTypedArray()

        fun from(prosentsats: Int) =
            values.singleOrNull { it.prosentsats == prosentsats }
                ?: throw RuntimeException("Ugyldig prosentsats for uttaksgrad: $prosentsats")
    }
}
