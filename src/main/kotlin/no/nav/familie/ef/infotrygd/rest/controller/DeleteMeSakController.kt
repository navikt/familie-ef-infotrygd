package no.nav.familie.ef.infotrygd.rest.controller

import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSak
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResultat
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@Unprotected
class DeleteMeTestController {
    @GetMapping(path = ["/springmappertest"])
    fun finnSaker(
    ): ResponseEntity<Any> {
        val infotrygdSak =
            InfotrygdSak(
                personIdent = "12345678910",
                id = 123L,
                saksnr = "123456789",
                saksblokk = "saksblokk",
                registrertDato = LocalDate.now(),
                mottattDato = LocalDate.now(),
                kapittelnr = "sd",
                stønadType = StønadType.OVERGANGSSTØNAD,
                undervalg = null,
                type = null,
                nivå = null,
                resultat = InfotrygdSakResultat.ÅPEN_SAK,
                vedtaksdato = LocalDate.now(),
                iverksattdato = LocalDate.now(),
                årsakskode = "å ikke være null er målet her",
                behandlendeEnhet = null,
                registrertAvEnhet = null,
                tkNr = null,
                region = null,
            )

        val body = InfotrygdSakResponse(listOf(infotrygdSak))
        return ResponseEntity.ok(body)
    }
}
