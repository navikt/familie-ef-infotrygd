package no.nav.familie.ef.infotrygd.rest.controller

import io.micrometer.core.annotation.Timed
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.repository.PeriodeRepository
import no.nav.familie.ef.infotrygd.rest.ApiExceptionHandler
import no.nav.familie.ef.infotrygd.rest.api.Periode
import no.nav.familie.ef.infotrygd.rest.api.PeriodeArenaRequest
import no.nav.familie.ef.infotrygd.rest.api.PeriodeArenaResponse
import no.nav.familie.ef.infotrygd.rest.api.PeriodeBarnetilsynRequest
import no.nav.familie.ef.infotrygd.rest.api.PeriodeMedBarn
import no.nav.familie.ef.infotrygd.rest.api.PeriodeRequest
import no.nav.familie.ef.infotrygd.rest.api.PeriodeResponse
import no.nav.familie.ef.infotrygd.service.PeriodeService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/perioder")
@Timed(value = "infotrygd_historikk_enslig_forsoerger_controller", percentiles = [0.5, 0.95])
@ProtectedWithClaims(issuer = "azure")
class PeriodeController(private val periodeRepository: PeriodeRepository, private val periodeService: PeriodeService) {

    @ApiOperation("Henter perioder")
    @PostMapping
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = "request",
                    dataType = "PeriodeRequest",
                    value = "{\n  \"identer\": [\n\"<fnr>\"\n],\n" +
                            " \"stønadstyper\": [\n\"OVERGANGSSTØNAD\"\n] \n}"
            )
    )
    fun hentPerioder(@RequestBody request: PeriodeRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }
        val perioder = periodeService.hentPerioder(request)
        return ResponseEntity.ok(
            PeriodeResponse(
                overgangsstønad = perioder.getOrDefault(StønadType.OVERGANGSSTØNAD, emptyList()),
                barnetilsyn = perioder.getOrDefault(StønadType.BARNETILSYN, emptyList()),
                skolepenger = perioder.getOrDefault(StønadType.SKOLEPENGER, emptyList())
            ))
    }


    /**
     * Denne henter perioder for alle typer EF-stønader, då arena ønsker de sammenslåtte
     */
    @ApiOperation("Henter perioder for Arena")
    @PostMapping(path = ["/arena", "/overgangsstonad"])
    @ApiImplicitParams(
            ApiImplicitParam(
                    name = "request",
                    dataType = "PeriodeArenaRequest",
                    value = "{\n  \"identer\": [\n\"<fnr>\"\n],\n" +
                            " \"fomDato\": \"2020-01-01\",\n  \"tomDato\": \"2021-01-01\"\n}"
            )
    )
    fun hentPerioderForArena(@RequestBody request: PeriodeArenaRequest): ResponseEntity<Any> {
        if (request.personIdenter.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val perioder = periodeRepository.hentPerioderForArena(request)
        return ResponseEntity.ok(PeriodeArenaResponse(perioder))
    }

    /**
     * Denne henter perioder for alle typer EF-stønader, då arena ønsker de sammenslåtte
     */
    @GetMapping(path = ["/migreringspersoner"])
    fun hentMigreringspersoner(@RequestParam antall: Int): ResponseEntity<Any> {
        val personerForMigrering = periodeRepository.hentPersonerForMigrering(antall)
        return ResponseEntity.ok(personerForMigrering)
    }

}
