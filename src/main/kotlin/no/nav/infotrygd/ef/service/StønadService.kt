package no.nav.infotrygd.ef.service

import no.nav.infotrygd.ef.repository.InfotrygdRepository
import no.nav.infotrygd.ef.repository.SakRepository
import no.nav.infotrygd.ef.rest.api.InfotrygdFinnesResponse
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import no.nav.infotrygd.ef.rest.api.Vedtakstreff
import org.springframework.stereotype.Service

@Service
class StønadService(private val infotrygdRepository: InfotrygdRepository,
                    private val sakRepository: SakRepository) {

    fun finnesIInfotrygd(request: InfotrygdSøkRequest): InfotrygdFinnesResponse {
        val harVedtak = infotrygdRepository.harStønad(request.personIdenter)
        val harAktivVedtak = infotrygdRepository.harAktivStønad(request.personIdenter).toSet()
        val saker = sakRepository.finnesSaker(request.personIdenter)
        val vedtak = harVedtak.map { (fnr, stønadType) ->
            Vedtakstreff(fnr, stønadType, harAktivVedtak.contains(Pair(fnr, stønadType)))
        }
        return InfotrygdFinnesResponse(vedtak = vedtak,
                                       saker = saker)
    }
}