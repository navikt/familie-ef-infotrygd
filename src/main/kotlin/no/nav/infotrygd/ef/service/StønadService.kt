package no.nav.infotrygd.ef.service

import no.nav.infotrygd.ef.repository.PersonRepository
import no.nav.infotrygd.ef.repository.StønadRepository
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import org.springframework.stereotype.Service

@Service
class StønadService(
    private val personRepository: PersonRepository,
    private val stonadRepository: StønadRepository
) {

    fun finnes(request: InfotrygdSøkRequest): Boolean {
        //TODO må legge til stønadstype
        return personRepository.findByFnrList(request.identer).isNotEmpty()
    }

    fun mottarStønad(request: InfotrygdSøkRequest): Boolean {
        //TODO må legge til stønadstype
        return personRepository.findByFnrList(request.identer)
            .flatMap { stonadRepository.findByPersonKeyAndRegion(it.personKey, it.region) }
            .isNotEmpty()
    }
}