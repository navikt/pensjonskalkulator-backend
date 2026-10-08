package no.nav.pensjon.kalkulator.avtale.client.np.compare

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.general.Alder

object PensjonsavtaleComparator {
    private val log = KotlinLogging.logger {}

    /**
     * Det antas at det meste er likt og eneste forskjeller er i sluttAlder.
     * Kapitalbevis og lignende produkter blir slått sammen med innskudd pensjon i REST og beløpene summeres.
     * Vi forsøker ikke å håndtere brukere som har flere avtaler da dette vil kreve mer kompleks logikk.
     *
     */
    fun finnDiff(avtalerFraSoap: Pensjonsavtaler, avtalerFraRest: Pensjonsavtaler): Boolean {
        var soap: List<ForenkletPensjonsavtale> = ComparatorMapper.mapToForenklet(avtalerFraSoap)
        val rest: List<ForenkletPensjonsavtale> = ComparatorMapper.mapToForenklet(avtalerFraRest)

        //vi tar kun to avtaler som ble sammenslått for å redusere støy i loggene, ellers blir det fort komplsert
        if (soap.size == 2 && rest.size == 1
            && soap[0].startAar == soap[1].startAar
            && soap[0].sluttAar == soap[1].sluttAar
        ) {
            soap = aggregateTilEnForenkletPensjonsavtale(soap)
            log.warn { "Norsk pensjon har sammenslått to avtaler fra SOAP til én i REST" }
        }

        if (soap != rest) {
            if (soap.size == rest.size) {
                val harDiff = soap.zip(rest).any { (soapAvtale, restAvtale) ->
                    soapAvtale.utbetalingsperioder.size != restAvtale.utbetalingsperioder.size ||
                            !soapAvtale.utbetalingsperioder.all { soapPeriode ->
                                restAvtale.utbetalingsperioder.any {
                                    soapPeriode.aarligUtbetalingForventet == it.aarligUtbetalingForventet &&
                                            soapPeriode.startAlder == it.startAlder &&
                                            erLikInnenforEnMaaned(soapPeriode.sluttAlder, it.sluttAlder)
                                }
                            }
                }
                if (!harDiff) {
                    return false
                }
            }
            log.warn { "Ulikheter i pensjonsavtaler fra SOAP og REST: SOAP: $soap, REST: $rest" }
            return true
        }
        return false
    }

    private fun aggregateTilEnForenkletPensjonsavtale(forenkletSoap: List<ForenkletPensjonsavtale>): List<ForenkletPensjonsavtale> {
        return listOf(
            ForenkletPensjonsavtale(
                startAar = forenkletSoap[0].startAar,
                sluttAar = forenkletSoap[0].sluttAar,
                utbetalingsperioder = forenkletSoap.flatMap { it.utbetalingsperioder }
                    .groupBy { Pair(it.startAlder, it.sluttAlder) }
                    .map { (key, value) ->
                        ForenkletUtbetalingsperiode(
                            aarligUtbetalingForventet = value.sumOf { it.aarligUtbetalingForventet },
                            startAlder = key.first,
                            sluttAlder = key.second
                        )
                    }
            )
        )
    }


    private fun erLikInnenforEnMaaned(
        soapAlder: Alder?,
        restAlder: Alder?
    ): Boolean =
        when {
            soapAlder == null || restAlder == null -> soapAlder == restAlder
            else -> soapAlder == restAlder || soapAlder.plussMaaneder(1) == restAlder
        }
}
