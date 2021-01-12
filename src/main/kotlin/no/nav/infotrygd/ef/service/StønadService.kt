package no.nav.infotrygd.ef.service

import no.nav.infotrygd.ef.model.StønadType
import no.nav.infotrygd.ef.repository.InfotrygdRepository
import no.nav.infotrygd.ef.rest.api.SøkFlereStønaderRequest
import no.nav.infotrygd.ef.rest.api.StønadTreff
import org.springframework.stereotype.Service

@Service
class StønadService(private val infotrygdRepository: InfotrygdRepository) {

    fun eksistererStønad(request: SøkFlereStønaderRequest): Map<StønadType, StønadTreff> {
        val harStønad = infotrygdRepository.harStønad(request.identer, request.stønader)
        val harAktivStønad = infotrygdRepository.harStønad(request.identer, request.stønader, kunAktive = true)
        return request.stønader.map {
            it to StønadTreff(eksisterer = harStønad.getOrDefault(it, false),
                              harAktivStønad = harAktivStønad.getOrDefault(it, false))
        }.toMap()
    }
}