package no.nav.familie.ef.infotrygd.rest.controller

import no.nav.familie.ef.infotrygd.repository.SakRepository
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/test")
@Unprotected
@Profile("dev")
class DeleteMeSakController(
    private val sakRepository: SakRepository,
) {

    @PostMapping(path = ["/finn"])
    fun finnSaker(
        @RequestBody request: InfotrygdSøkRequest,
    ): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(InfotrygdSakResponse(sakRepository.finnSaker(request.personIdenter)))
    }
}
