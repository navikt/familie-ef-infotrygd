package no.nav.familie.ef.infotrygd.rest.controller

import io.micrometer.core.annotation.Timed
import no.nav.familie.ef.infotrygd.repository.SakRepository
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/saker")
@Timed(value = "infotrygd_sak_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class SakController(private val sakRepository: SakRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping(path = ["/finn"])
    fun finnSaker(
        @RequestBody request: InfotrygdSøkRequest,
    ): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(InfotrygdSakResponse(sakRepository.finnSaker(request.personIdenter)))
    }

    @GetMapping(path = ["/hentrapport"])
    fun finnÅpneSaker(): ResponseEntity<Any> {
        val hentÅpneSaker = sakRepository.hentÅpneSaker()
        logger.info("Åpne saker: ${hentÅpneSaker.typeMedAntall}")
        return ResponseEntity.ok(hentÅpneSaker)
    }
}
