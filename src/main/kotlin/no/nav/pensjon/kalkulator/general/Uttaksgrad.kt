package no.nav.pensjon.kalkulator.general

enum class Uttaksgrad(val prosentsats: Int) {
    NULL(prosentsats = 0),
    TJUE_PROSENT(prosentsats = 20),
    FOERTI_PROSENT(prosentsats = 40),
    FEMTI_PROSENT(prosentsats = 50),
    SEKSTI_PROSENT(prosentsats = 60),
    AATTI_PROSENT(prosentsats = 80),
    HUNDRE_PROSENT(prosentsats = 100);

    companion object {
        private val valuesByProsentsats = entries.associateBy { it.prosentsats }

        fun from(prosentsats: Int) =
            valuesByProsentsats[prosentsats]
                ?: throw IllegalArgumentException("Ugyldig prosentsats for uttaksgrad $prosentsats")
    }
}