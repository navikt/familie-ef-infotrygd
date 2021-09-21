package no.nav.infotrygd.ef.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.infotrygd.ef.repository.PeriodeRepository
import no.nav.infotrygd.ef.rest.api.PeriodeArenaRequest
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

    /**
     * Denne henter perioder for alle typer EF-stønader, då arena ønsker de sammenslåtte
     */
    @ApiOperation("Henter perioder")
    @PostMapping(path = ["/arena", "/overgangsstonad"])
    @ApiImplicitParams(ApiImplicitParam(
            name = "request",
            dataType = "PeriodeOvergangsstønadRequest",
            value = "{\n  \"identer\": [\n\"01015450301\"\n],\n" +
                    " \"fomDato\": \"2020-01-01\",\n  \"tomDato\": \"2021-01-01\"\n}"
    ))
    fun hentPerioder(@RequestBody request: PeriodeArenaRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val perioder = periodeRepository.hentPerioder(request)
        return ResponseEntity.ok(PeriodeResponse(perioder))
    }

}