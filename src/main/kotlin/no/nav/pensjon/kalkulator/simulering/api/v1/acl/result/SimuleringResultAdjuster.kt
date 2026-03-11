package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

object SimuleringResultAdjuster {

    /**
     * Assign a pension with age 0 to the current age or remove it from the list if the current age already exists.
     */
    fun justerAlderspensjonListe(
        pensjonListe: List<SimuleringV1Alderspensjon>,
        naavaerendeAlderAar: Int
    ): List<SimuleringV1Alderspensjon> =
        pensjonListe
            .firstOrNull { it.alderAar == 0 }
            ?.let { listeMedAlderAar(pensjonListe, pensjonForAlder0 = it, naavaerendeAlderAar) }
            ?.sortedBy { it.alderAar }
            ?: pensjonListe

    /**
     * Assign privat AFP with age 0 to the current age or remove it from the list if the current age already exists.
     */
    fun justerPrivatAfpListe(
        pensjonListe: List<SimuleringV1PrivatAfp>,
        naavaerendeAlderAar: Int
    ): List<SimuleringV1PrivatAfp> =
        pensjonListe
            .firstOrNull { it.alderAar == 0 }
            ?.let { listeMedAlderAar(pensjonListe, pensjonForAlder0 = it, naavaerendeAlderAar) }
            ?.sortedBy { it.alderAar }
            ?: pensjonListe

    private fun listeMedAlderAar(
        pensjonListe: List<SimuleringV1Alderspensjon>,
        pensjonForAlder0: SimuleringV1Alderspensjon,
        alderAar: Int
    ): List<SimuleringV1Alderspensjon> {
        val listeUtenAlder0 = pensjonListe.filter { it.alderAar != 0 }.toMutableList()

        if (listeUtenAlder0.any { it.alderAar == alderAar }) return listeUtenAlder0

        listeUtenAlder0.add(
            SimuleringV1Alderspensjon(
                alderAar,
                beloep = pensjonForAlder0.beloep,
                gjenlevendetillegg = pensjonForAlder0.gjenlevendetillegg,
                extension = pensjonForAlder0.extension
            )
        )

        return listeUtenAlder0
    }

    private fun listeMedAlderAar(
        pensjonListe: List<SimuleringV1PrivatAfp>,
        pensjonForAlder0: SimuleringV1PrivatAfp,
        alderAar: Int
    ): List<SimuleringV1PrivatAfp> {
        val listeUtenAlder0 = pensjonListe.filter { it.alderAar != 0 }.toMutableList()

        if (listeUtenAlder0.any { it.alderAar == alderAar }) return listeUtenAlder0

        listeUtenAlder0.add(
            SimuleringV1PrivatAfp(
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
