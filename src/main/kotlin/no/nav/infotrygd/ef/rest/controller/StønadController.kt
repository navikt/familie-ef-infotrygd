package no.nav.infotrygd.ef.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.infotrygd.ef.rest.api.SøkFlereStønaderRequest
import no.nav.infotrygd.ef.rest.api.FinnesResponse
import no.nav.infotrygd.ef.service.StønadService
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
@RequestMapping("/api/stonad",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
@Timed(value = "infotrygd_historikk_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class StønadController(private val stønadService: StønadService) {

    @ApiOperation("Søker etter oppgitte fødselssnummere med stønadstype")
    @PostMapping(path = ["/finnes"])
    @ApiImplicitParams(ApiImplicitParam(
            name = "request",
            dataType = "SøkFlereStønaderRequest",
            value = "{\n  \"identer\": [\n\"01015450301\"\n]," + "\n  \"stønader\": [\n\"OVERGANGSSTØNAD\"\n]\n}"
    ))
    fun harStønad(@RequestBody request: SøkFlereStønaderRequest): ResponseEntity<Any> {
        if (request.identer.isEmpty() || request.stønader.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val stønader = stønadService.harStønad(request)
        return ResponseEntity.ok(FinnesResponse(stønader))
    }

}