package no.nav.familie.ef.infotrygd.rest.controller

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ping", produces = [MediaType.TEXT_PLAIN_VALUE])
@Unprotected
class PingController {

    @GetMapping
    fun ping() = "pong"
}
