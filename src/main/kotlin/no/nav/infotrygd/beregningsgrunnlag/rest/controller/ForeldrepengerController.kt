package no.nav.infotrygd.beregningsgrunnlag.rest.controller

import no.nav.infotrygd.beregningsgrunnlag.dto.Foreldrepenger
import no.nav.infotrygd.beregningsgrunnlag.dto.Svangerskapspenger
import no.nav.infotrygd.beregningsgrunnlag.model.kodeverk.Stoenadstype
import no.nav.infotrygd.beregningsgrunnlag.service.ClientValidator
import no.nav.infotrygd.beregningsgrunnlag.service.ForeldrepengerService
import no.nav.infotrygd.beregningsgrunnlag.service.SvangerskapspengerService
import no.nav.infotrygd.beregningsgrunnlag.values.FodselNr
import no.nav.security.oidc.api.Protected
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Protected
@RestController
@RequestMapping("/foreldrepenger")
class ForeldrepengerController(
    private val foreldrepengerService: ForeldrepengerService,
    private val svangerskapspengerService: SvangerskapspengerService,
    private val clientValidator: ClientValidator
) {
    @GetMapping(path = ["/adopsjon"])
    fun adopsjon(@RequestParam
                       fodselNr: String,

                       @RequestParam
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       fom: LocalDate,

                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       tom: LocalDate?) : List<Foreldrepenger> {

        clientValidator.authorizeClient()
        return foreldrepengerService.hentForeldrepenger(listOf(Stoenadstype.ADOPSJON), FodselNr(fodselNr), fom, tom)
    }

    @GetMapping(path = ["/foedsel"])
    fun foedsel(@RequestParam
                       fodselNr: String,

                       @RequestParam
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       fom: LocalDate,

                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       tom: LocalDate?) : List<Foreldrepenger> {

        clientValidator.authorizeClient()
        return foreldrepengerService.hentForeldrepenger(listOf(Stoenadstype.FOEDSEL), FodselNr(fodselNr), fom, tom)
    }

    @GetMapping(path = ["/svangerskap"])
    fun svangerskap(@RequestParam
                fodselNr: String,

                @RequestParam
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                fom: LocalDate,

                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                tom: LocalDate?) : List<Svangerskapspenger> {

        clientValidator.authorizeClient()
        return svangerskapspengerService.hentSvangerskapspenger(FodselNr(fodselNr), fom, tom)
    }
}