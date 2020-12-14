package no.nav.infotrygd.ef.rest.controller

import no.nav.infotrygd.ef.repository.PersonRepository
import no.nav.infotrygd.ef.repository.StønadRepository
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkResponse
import no.nav.infotrygd.ef.rest.api.StønadType
import no.nav.infotrygd.ef.testutil.TestData
import no.nav.infotrygd.ef.testutil.restClient
import no.nav.infotrygd.ef.testutil.restClientNoAuth
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var stønadRepository: StønadRepository

    private val uri = "/infotrygd/enslig-forsoerger/personsok"
    private val uri2 = "/infotrygd/enslig-forsoerger/lopendeSak"

    @Test
    fun `infotrygd historikk søk`() {
        val person = TestData.person()
        val ukjentPerson = TestData.person()

        personRepository.saveAndFlush(person)

        val requestMedPersonSomFinnes = InfotrygdSøkRequest(listOf(person.fnr), StønadType.OVERGANGSSTØNAD)
        val requestMedUkjentPerson = InfotrygdSøkRequest(listOf(ukjentPerson.fnr), StønadType.OVERGANGSSTØNAD)

        val client = restClient(port)

        val res1 = kallStønadController(uri, client, requestMedPersonSomFinnes).responseBody()
        val res2 = kallStønadController(uri, client, requestMedUkjentPerson).responseBody()
        val resFraTomRequest = kallStønadController(uri, client).responseBody()

        Assertions.assertThat(res1.ingenTreff).isFalse()
        Assertions.assertThat(res2.ingenTreff).isTrue()
        Assertions.assertThat(resFraTomRequest.ingenTreff).isTrue()
    }

    @Test
    fun `infotrygdsøk etter løpende barnetrygd`() {
        val person = TestData.person()
        val ukjentPerson = TestData.person()
        val stønad = TestData.stønad(person)
        val stønad2 = TestData.stønad(ukjentPerson, opphørtFom = "111111")

        personRepository.saveAndFlush(person)
        stønadRepository.saveAll(listOf(stønad, stønad2))

        val requestMedPersonMedLøpendeSak = InfotrygdSøkRequest(listOf(person.fnr), StønadType.OVERGANGSSTØNAD)
        val requestMedUkjentPerson = InfotrygdSøkRequest(listOf(ukjentPerson.fnr), StønadType.OVERGANGSSTØNAD)

        val client = restClient(port)

        val res1 = kallStønadController(uri2, client, requestMedPersonMedLøpendeSak).responseBody()
        val res2 = kallStønadController(uri2, client, requestMedUkjentPerson).responseBody()
        val resFraTomRequest = kallStønadController(uri2, client).responseBody()

        Assertions.assertThat(res1.ingenTreff).isFalse()
        Assertions.assertThat(res2.ingenTreff).isTrue()
        Assertions.assertThat(resFraTomRequest.ingenTreff).isTrue()
    }

    @Test
    fun noAuth() {
        val client = restClientNoAuth(port)
        val result = kallStønadController(uri, client)
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun clientAuth() {
        val client = restClient(port, subject = "wrong")
        val result = kallStønadController(uri, client)
        Assertions.assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun kallStønadController(
        uri: String,
        client: WebClient,
        request: InfotrygdSøkRequest = InfotrygdSøkRequest(listOf(), StønadType.OVERGANGSSTØNAD)
    ): ClientResponse {
        return client.post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_JSON)
            .syncBody(request)
            .exchange()
            .block()!!
    }
}

private fun ClientResponse.responseBody(): InfotrygdSøkResponse {
    return this.bodyToMono(InfotrygdSøkResponse::class.java).block()!!
}
