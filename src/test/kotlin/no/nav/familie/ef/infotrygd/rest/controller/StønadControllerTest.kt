package no.nav.familie.ef.infotrygd.rest.controller

import no.nav.familie.ef.infotrygd.rest.api.InfotrygdFinnesResponse
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSøkRequest
import no.nav.familie.ef.infotrygd.testutils.TestData
import no.nav.familie.ef.infotrygd.testutils.restClient
import no.nav.familie.ef.infotrygd.testutils.restClientNoAuth
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableMockOAuth2Server
class StønadControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    private val personsøkPath = "/stonad/eksisterer"

    private val fnr = TestData.fnrGenerator.foedselsnummer().asString

    @Test
    fun `request for HarStønad uten match`() {
        val request = InfotrygdSøkRequest(setOf(fnr))

        val client = restClient(mockOAuth2Server, port)

        val response = kallStønadController(personsøkPath, client, request).responseBody()

        assertThat(response.vedtak).isEmpty()
        assertThat(response.saker).isEmpty()
    }

    @Test
    fun `request for HarStønad uten fnr skal kaste bad request`() {
        val client = restClient(mockOAuth2Server, port)
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
        val client = restClient(mockOAuth2Server, port, accessAsApplication = false)
        val result = kallStønadController(personsøkPath, client, InfotrygdSøkRequest(emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun kallStønadController(
        uri: String,
        client: WebClient,
        request: InfotrygdSøkRequest
    ): WebClient.RequestHeadersSpec<*> {
        return client.post()
            .uri("/api$uri")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
    }
}

private fun WebClient.RequestHeadersSpec<*>.statusCode(): HttpStatus =
    this.exchangeToMono { it.toBodilessEntity() }.block()!!.statusCode

private fun WebClient.RequestHeadersSpec<*>.responseBody(): InfotrygdFinnesResponse =
    this.retrieve().toEntity<InfotrygdFinnesResponse>().block()!!.body!!
