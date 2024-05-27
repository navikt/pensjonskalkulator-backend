package no.nav.pensjon.kalkulator.omstillingsstoenad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import org.springframework.stereotype.Service

@Service
class OmstillingOgGjenlevendeYtelseService(
    private val omstillingsstoenadService: OmstillingsstoenadService,
    private val sakService: SakService,
) {

    suspend fun harLoependeSaker(): Boolean {
        return withContext(Dispatchers.IO + SecurityCoroutineContext()) {
            val omstillingsstoenadDeferred = async { omstillingsstoenadService.mottarOmstillingsstoenad() }
            val gjenlevendeYtelseDeferred = async { sakService.harSakType(SakType.GJENLEVENDEYTELSE) }

            val mottarOmstillingsstoenad = omstillingsstoenadDeferred.await()
            val mottarGjenlevendeYtelse = gjenlevendeYtelseDeferred.await()

            mottarOmstillingsstoenad || mottarGjenlevendeYtelse
        }
    }
}