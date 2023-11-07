package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.SkjermingService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class GroupMembershipServiceTest {

    private lateinit var groupMembershipService: GroupMembershipService

    @Mock
    private lateinit var groupService: GroupService

    @Mock
    private lateinit var skjermingService: SkjermingService

    @Mock
    private lateinit var adresseService: FortroligAdresseService

    @BeforeEach
    fun initialize() {
        groupMembershipService = GroupMembershipService(
            "saksbehandler-gruppa",
            "egne-ansatte-gruppa",
            "fortrolig-adresse-gruppa",
            "strengt-fortrolig-adresse-gruppa",
            groupService,
            skjermingService,
            adresseService
        )
    }

    @Test
    fun `innlogget bruker har i utgangspunktet ikke tilgang`() {
        `when`(groupService.groups()).thenReturn(emptyList())
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `alminnelig saksbehandler har tilgang til ubeskyttet person`() {
        `when`(groupService.groups()).thenReturn(listOf("saksbehandler-gruppa"))
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertTrue(harTilgang)
    }

    @Test
    fun `alminnelig saksbehandler har ikke tilgang til beskyttet person`() {
        `when`(groupService.groups()).thenReturn(listOf("saksbehandler-gruppa"))
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `saksbehandler med fortrolig tilgang har ikke tilgang til personer med strengt fortrolig adresse`() {
        `when`(groupService.groups()).thenReturn(listOf("saksbehandler-gruppa", "fortrolig-adresse-gruppa"))
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.STRENGT_FORTROLIG)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `saksbehandler med strengt fortrolig tilgang har ikke tilgang til skjermet person`() {
        `when`(groupService.groups()).thenReturn(listOf("saksbehandler-gruppa", "strengt-fortrolig-adresse-gruppa"))
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }

    @Test
    fun `saksbehandler med full tilgang har tilgang til skjermede personer med strengt fortrolig adresse`() {
        `when`(groupService.groups()).thenReturn(
            listOf(
                "saksbehandler-gruppa",
                "egne-ansatte-gruppa",
                "fortrolig-adresse-gruppa",
                "strengt-fortrolig-adresse-gruppa"
            )
        )
        arrangeBeskyttelse(personErSkjermet = true, AdressebeskyttelseGradering.STRENGT_FORTROLIG)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertTrue(harTilgang)
    }

    @Test
    fun `saksbehandler med kun tilleggstilganger har ikke tilgang til applikasjonen`() {
        `when`(groupService.groups()).thenReturn(
            listOf(
                "egne-ansatte-gruppa",
                "fortrolig-adresse-gruppa",
                "strengt-fortrolig-adresse-gruppa"
                // mangler grunntilgang (saksbehandler-gruppa)
            )
        )
        arrangeBeskyttelse(personErSkjermet = false, AdressebeskyttelseGradering.UGRADERT)

        val harTilgang = groupMembershipService.innloggetBrukerHarTilgang(pid)

        assertFalse(harTilgang)
    }


    private fun arrangeBeskyttelse(personErSkjermet: Boolean, gradering: AdressebeskyttelseGradering) {
        `when`(skjermingService.personErTilgjengelig(pid)).thenReturn(!personErSkjermet)
        `when`(adresseService.adressebeskyttelseGradering(pid)).thenReturn(gradering)
    }
}
