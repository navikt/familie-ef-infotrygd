package no.nav.familie.ef.infotrygd.testutils

import no.nav.familie.ef.infotrygd.testutils.TokenUtil.clientToken
import no.nav.security.mock.oauth2.MockOAuth2Server
import org.springframework.web.reactive.function.client.WebClient

fun restClient(mockOAuth2Server: MockOAuth2Server, port: Int, clientId: String = "CLIENT_ID_EF_SAK", accessAsApplication: Boolean = true): WebClient {
    val token = clientToken(mockOAuth2Server, clientId, accessAsApplication)

    return WebClient.builder()
        .baseUrl(baseUrl(port))
        .defaultHeader("Authorization", "Bearer $token")
        .build()
}

fun restClientNoAuth(port: Int): WebClient {
    return WebClient.builder()
        .baseUrl("http://localhost:$port")
        .build()
}

private fun baseUrl(port: Int) = "http://localhost:$port"
