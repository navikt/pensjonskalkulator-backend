package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.Pid

/**
 * Personen hvis data vises i kalkulatoren er gitt ved feltet 'pid' (person-ID).
 * Det kan være en annen person enn den som er innlogget, siden kalkulatoren støtter representasjonsforhold.
 * Representasjonsforholdet er gitt ved feltet 'rolle' (forholdet personen har til den som er innlogget).
 */
data class RepresentasjonTarget(
    val pid: Pid? = null,
    val rolle: RepresentertRolle
)
