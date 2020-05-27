package no.nav.infotrygd.barnetrygd.dto

import no.nav.infotrygd.barnetrygd.model.kodeverk.Stoenadstype
import no.nav.infotrygd.barnetrygd.model.kodeverk.Tema

fun vedtakToSakDto(vedtak: no.nav.infotrygd.barnetrygd.model.db2.Vedtak): SakDto {
    return SakDto(
        sakId = null,
        tema = Tema.PAAROERENDE_SYKDOM.toDto(),
        behandlingstema = Stoenadstype.PLEIEPENGER_NY_ORDNING.toDto(),
        type = null,
        status = null,
        resultat = null,
        vedtatt = vedtak.stonad.tidspunktRegistrert.toLocalDate(),
        iverksatt = vedtak.stonad.datoStart,
        registrert = vedtak.stonad.tidspunktRegistrert.toLocalDate(),
        opphoerFom = vedtak.stonad.datoOpphoer
    )
}