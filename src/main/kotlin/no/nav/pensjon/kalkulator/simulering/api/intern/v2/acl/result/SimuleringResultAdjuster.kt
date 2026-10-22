package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

object SimuleringResultAdjuster {

    /**
     * Assign a pension with age 0 to the current age or remove it from the list if the current age already exists.
     */
    fun justerAlderspensjonListe(
        pensjonListe: List<SimuleringV2Alderspensjon>,
        naavaerendeAlderAar: Int
    ): List<SimuleringV2Alderspensjon> =
        pensjonListe
            .firstOrNull { it.alderAar == 0 }
            ?.let { listeMedAlderAar(pensjonListe, pensjonForAlder0 = it, naavaerendeAlderAar) }
            ?.sortedBy { it.alderAar }
            ?: pensjonListe

    /**
     * Assign privat AFP with age 0 to the current age or remove it from the list if the current age already exists.
     */
    fun justerPrivatAfpListe(
        pensjonListe: List<SimuleringV2PrivatAfp>,
        naavaerendeAlderAar: Int
    ): List<SimuleringV2PrivatAfp> =
        pensjonListe
            .firstOrNull { it.alderAar == 0 }
            ?.let { listeMedAlderAar(pensjonListe, pensjonForAlder0 = it, naavaerendeAlderAar) }
            ?.sortedBy { it.alderAar }
            ?: pensjonListe

    private fun listeMedAlderAar(
        pensjonListe: List<SimuleringV2Alderspensjon>,
        pensjonForAlder0: SimuleringV2Alderspensjon,
        alderAar: Int
    ): List<SimuleringV2Alderspensjon> {
        val listeUtenAlder0 = pensjonListe.filter { it.alderAar != 0 }.toMutableList()

        if (listeUtenAlder0.any { it.alderAar == alderAar }) return listeUtenAlder0

        listeUtenAlder0.add(
            pensjonForAlder0.copy(alderAar = alderAar)
        )

        return listeUtenAlder0
    }

    private fun listeMedAlderAar(
        pensjonListe: List<SimuleringV2PrivatAfp>,
        pensjonForAlder0: SimuleringV2PrivatAfp,
        alderAar: Int
    ): List<SimuleringV2PrivatAfp> {
        val listeUtenAlder0 = pensjonListe.filter { it.alderAar != 0 }.toMutableList()

        if (listeUtenAlder0.any { it.alderAar == alderAar }) return listeUtenAlder0

        listeUtenAlder0.add(
            SimuleringV2PrivatAfp(
                alderAar,
                aarligBeloep = pensjonForAlder0.aarligBeloep,
                kompensasjonstillegg = pensjonForAlder0.kompensasjonstillegg,
                kronetillegg = pensjonForAlder0.kronetillegg,
                livsvarig = pensjonForAlder0.livsvarig,
                maanedligBeloep = pensjonForAlder0.maanedligBeloep
            )
        )

        return listeUtenAlder0
    }
}
