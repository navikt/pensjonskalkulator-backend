package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import org.springframework.stereotype.Service

@Service
class LoependeVedtakService(
    private val vedtakClient: LoependeVedtakClient,
    private val personService: PersonService,
    private val pidGetter: PidGetter
) {
    fun hentLoependeVedtak(): VedtakSamling {
        val vedtakSamling = vedtakClient.hentLoependeVedtak(pidGetter.pid())

        return vedtakSamling.gjenlevenderett
            ?.let { medAvdoedNavn(vedtakSamling, it.avdoedPid) }
            ?: vedtakSamling
    }

    private fun medAvdoedNavn(vedtakSamling: VedtakSamling, avdoedPid: Pid): VedtakSamling =
        try {
            vedtakSamling.medAvdoedNavn(personService.getPerson(avdoedPid).navn)
        } catch (_: NotFoundException) {
            vedtakSamling
        }
}