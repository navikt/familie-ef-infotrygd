package no.nav.familie.ef.infotrygd.rest.controller

import no.nav.familie.ef.infotrygd.rest.api.InfotrygdFinnesResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.familie.ef.infotrygd.testutil.TestData
import no.nav.familie.ef.infotrygd.testutil.restClient
import no.nav.familie.ef.infotrygd.testutil.restClientNoAuth
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StønadControllerTest {

    @LocalServerPort
    private var port: Int = 0

    private val personsøkPath = "/stonad/eksisterer"

    private val fnr = TestData.fnrGenerator.foedselsnummer().asString

    @Test
    fun `request for HarStønad uten match`() {
        val request = InfotrygdSøkRequest(setOf(fnr))

        val client = restClient(port)

        val response = kallStønadController(personsøkPath, client, request).responseBody()

        assertThat(response.vedtak).isEmpty()
        assertThat(response.saker).isEmpty()
    }

    @Test
    fun `request for HarStønad uten fnr skal kaste bad request`() {
        val client = restClient(port)
        val request = InfotrygdSøkRequest(emptySet())
        assertThat(kallStønadController(personsøkPath, client, request).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun noAuth() {
        val client = restClientNoAuth(port)
        val result = kallStønadController(personsøkPath, client, InfotrygdSøkRequest(emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `noAuth - ikke client credentials`() {
        val client = restClient(port, accessAsApplication = false)
        val result = kallStønadController(personsøkPath, client, InfotrygdSøkRequest(emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun kallStønadController(
        uri: String,
        client: WebClient,
        request: InfotrygdSøkRequest
    ): ClientResponse {
        return client.post()
            .uri("/api$uri")
            .contentType(MediaType.APPLICATION_JSON)
            .syncBody(request)
            .exchange()
            .block()!!
    }
}

private fun ClientResponse.responseBody(): InfotrygdFinnesResponse {
    return this.bodyToMono(InfotrygdFinnesResponse::class.java).block()!!
}
