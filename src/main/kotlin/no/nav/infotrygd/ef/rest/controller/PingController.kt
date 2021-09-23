package no.nav.infotrygd.ef.rest.controller

import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ping", produces = [MediaType.TEXT_PLAIN_VALUE])
@Unprotected
class PingController(@Value("\${APP_DATASOURCE_USERNAME}") private val username: String,
                     @Value("\${APP_DATASOURCE_USERNAME}") private val url: String) {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.info("Username: ${username[0]}..${username.substringAfterLast("_")}")
        logger.info("url: $url")
    }

    @GetMapping
    fun ping() = "pong"
}
