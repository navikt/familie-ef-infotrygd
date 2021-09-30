package no.nav.infotrygd.ef.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.infotrygd.ef.model.StønadType
import no.nav.infotrygd.ef.repository.PeriodeRepository
import no.nav.infotrygd.ef.rest.api.PeriodeArenaRequest
import no.nav.infotrygd.ef.rest.api.PeriodeArenaResponse
import no.nav.infotrygd.ef.rest.api.PeriodeRequest
import no.nav.infotrygd.ef.rest.api.PeriodeResponse
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
@RequestMapping("/api/perioder",
                consumes = [MediaType.APPLICATION_JSON_VALUE],
                produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Timed(value = "infotrygd_historikk_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class PeriodeController(private val periodeRepository: PeriodeRepository) {

    @ApiOperation("Henter perioder")
    @PostMapping(path = ["/"])
    @ApiImplicitParams(ApiImplicitParam(
            name = "request",
            dataType = "PeriodeRequest",
            value = "{\n  \"identer\": [\n\"<fnr>\"\n],\n" +
                    " \"stønadstyper\": [\n\"OVERGANGSSTØNAD\"\n] \n}"
    ))
    fun hentPerioder(@RequestBody request: PeriodeRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val perioder = periodeRepository.hentPerioder(request).groupBy({ it.first }) { it.second }
        return ResponseEntity.ok(PeriodeResponse(overgangsstønad = perioder.getOrDefault(StønadType.OVERGANGSSTØNAD, emptyList()),
                                                 barnetilsyn = perioder.getOrDefault(StønadType.BARNETILSYN, emptyList()),
                                                 skolepenger = perioder.getOrDefault(StønadType.SKOLEPENGER, emptyList())))
    }

    /**
     * Denne henter perioder for alle typer EF-stønader, då arena ønsker de sammenslåtte
     */
    @ApiOperation("Henter perioder for Arena")
    @PostMapping(path = ["/arena", "/overgangsstonad"])
    @ApiImplicitParams(ApiImplicitParam(
            name = "request",
            dataType = "PeriodeArenaRequest",
            value = "{\n  \"identer\": [\n\"<fnr>\"\n],\n" +
                    " \"fomDato\": \"2020-01-01\",\n  \"tomDato\": \"2021-01-01\"\n}"
    ))
    fun hentPerioderForArena(@RequestBody request: PeriodeArenaRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val perioder = periodeRepository.hentPerioderForArena(request)
        return ResponseEntity.ok(PeriodeArenaResponse(perioder))
    }

}