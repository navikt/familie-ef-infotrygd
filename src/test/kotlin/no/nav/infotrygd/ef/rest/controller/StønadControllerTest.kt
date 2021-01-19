package no.nav.infotrygd.ef.rest.controller

import no.nav.infotrygd.ef.model.StønadType
import no.nav.infotrygd.ef.rest.api.EksistererStønadResponse
import no.nav.infotrygd.ef.rest.api.SøkFlereStønaderRequest
import no.nav.infotrygd.ef.testutil.TestData
import no.nav.infotrygd.ef.testutil.restClient
import no.nav.infotrygd.ef.testutil.restClientNoAuth
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
        val request = SøkFlereStønaderRequest(setOf(fnr), setOf(StønadType.OVERGANGSSTØNAD))

        val client = restClient(port)

        val res1 = kallStønadController(personsøkPath, client, request).responseBody()

        val stønad = res1.stønader.get(StønadType.OVERGANGSSTØNAD) ?: error("Forventet att få tilbake data for forespurt type")
        assertThat(stønad.eksisterer).isFalse
        assertThat(stønad.harAktivStønad).isFalse
    }

    @Test
    fun `request for HarStønad uten fnr skal kaste bad request`() {
        val client = restClient(port)
        val request = SøkFlereStønaderRequest(emptySet(), setOf(StønadType.OVERGANGSSTØNAD))
        assertThat(kallStønadController(personsøkPath, client, request).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `request for HarStønad uten stønader skal kaste bad request`() {
        val client = restClient(port)
        val request = SøkFlereStønaderRequest(setOf(fnr), emptySet())
        assertThat(kallStønadController(personsøkPath, client, request).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun noAuth() {
        val client = restClientNoAuth(port)
        val result = kallStønadController(personsøkPath, client, SøkFlereStønaderRequest(emptySet(), emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `noAuth - feil system som kaller`() {
        val client = restClient(port, "Annet_System")
        val result = kallStønadController(personsøkPath, client, SøkFlereStønaderRequest(emptySet(), emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `noAuth - ikke client credentials`() {
        val client = restClient(port, accessAsApplication = false)
        val result = kallStønadController(personsøkPath, client, SøkFlereStønaderRequest(emptySet(), emptySet()))
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun kallStønadController(uri: String,
                                     client: WebClient,
                                     request: SøkFlereStønaderRequest): ClientResponse {
        return client.post()
                .uri("/api$uri")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(request)
                .exchange()
                .block()!!
    }

}

private fun ClientResponse.responseBody(): EksistererStønadResponse {
    return this.bodyToMono(EksistererStønadResponse::class.java).block()!!
}
