package no.nav.infotrygd.ef.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkResponse
import no.nav.infotrygd.ef.service.StønadService
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@Protected
@RestController
@Timed(value = "infotrygd_historikk_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class StønadController(private val stønadService: StønadService) {

    @ApiOperation("søker etter oppgitte fødselssnummere med stønadstype og gir svar 'ingenTreff=true/false' ang")
    @PostMapping(path = ["/infotrygd/enslig-forsoerger/personsok"], consumes = ["application/json"])
    @ApiImplicitParams(
        ApiImplicitParam(
            name = "request",
            dataType = "InfotrygdSøkRequest",
            value = "{\n  \"brukere\": [\n\"01015450301\"\n]," + "\n  \"stønad\": \"OVERGANGSSTØNAD\"\n}"
        )
    )
    fun finnesIInfotrygd(@RequestBody request: InfotrygdSøkRequest): ResponseEntity<Any> {
        if (request.brukere.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val finnes = stønadService.finnes(request)
        return ResponseEntity.ok(InfotrygdSøkResponse(ingenTreff = !finnes))
    }

    @ApiOperation("Avgjør hvorvidt det finens en løpende sak på søker i gitt stønad.")
    @PostMapping(path = ["/infotrygd/enslig-forsoerger/lopendeSak"], consumes = ["application/json"])
    @ApiImplicitParams(
        ApiImplicitParam(
            name = "request",
            dataType = "InfotrygdSøkRequest",
            value = "{\n  \"brukere\": [\n\"01015450301\"\n]," + "\n  \"stønad\": \"OVERGANGSSTØNAD\"\n}"
        )
    )
    fun harLopendeSak(@RequestBody request: InfotrygdSøkRequest): ResponseEntity<Any> {
        if (request.brukere.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val mottarStønad = stønadService.mottarStønad(request)
        return ResponseEntity.ok(InfotrygdSøkResponse(ingenTreff = !mottarStønad))
    }
}