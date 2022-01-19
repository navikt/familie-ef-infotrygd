package no.nav.familie.ef.infotrygd.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.familie.ef.infotrygd.repository.SakRepository
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.familie.ef.infotrygd.service.StønadService
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Protected
@RestController
@RequestMapping("/api/sak")
@Timed(value = "infotrygd_sak_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class SakController(private val sakRepository: SakRepository) {

    @PostMapping(path = ["/eksisterer"])
    fun finnSaker(@RequestBody request: InfotrygdSøkRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(sakRepository.finnSaker(request.personIdenter))
    }

}