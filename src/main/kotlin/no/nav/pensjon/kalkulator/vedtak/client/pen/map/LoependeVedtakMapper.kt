package no.nav.pensjon.kalkulator.vedtak.client.pen.map

object LoependeVedtakMapper {

    fun fromDto(dto: no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto): no.nav.pensjon.kalkulator.vedtak.LoependeVedtak {
        return no.nav.pensjon.kalkulator.vedtak.LoependeVedtak(
            alderspensjon = fromDto(dto.alderspensjon),
            ufoeretrygd = fromDto(dto.ufoeretrygd),
            afpPrivat = fromDto(dto.afpPrivat),
            afpOffentlig = fromDto(dto.afpOffentlig),
        )
    }

    private fun fromDto(dto: no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLopenedeVedtakMedGradDto?): no.nav.pensjon.kalkulator.vedtak.Grad? {
        return dto?.let {
            no.nav.pensjon.kalkulator.vedtak.Grad(
                grad = it.grad,
            )
        }
    }
}