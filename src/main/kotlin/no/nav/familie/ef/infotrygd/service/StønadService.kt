package no.nav.familie.ef.infotrygd.service

import no.nav.familie.ef.infotrygd.repository.InfotrygdRepository
import no.nav.familie.ef.infotrygd.repository.SakRepository
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdFinnesResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.familie.ef.infotrygd.rest.api.Vedtakstreff
import org.springframework.stereotype.Service

@Service
class StønadService(
    private val infotrygdRepository: InfotrygdRepository,
    private val sakRepository: SakRepository
) {

    fun finnesIInfotrygd(request: InfotrygdSøkRequest): InfotrygdFinnesResponse {
        val harVedtak = infotrygdRepository.harStønad(request.personIdenter)
        val harAktivVedtak = infotrygdRepository.harAktivStønad(request.personIdenter).toSet()
        val saker = sakRepository.finnesSaker(request.personIdenter)
        val vedtak = harVedtak.map { (fnr, stønadType) ->
            Vedtakstreff(fnr, stønadType, harAktivVedtak.contains(Pair(fnr, stønadType)))
        }
        return InfotrygdFinnesResponse(
            vedtak = vedtak,
            saker = saker
        )
    }
}
