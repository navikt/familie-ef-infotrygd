package no.nav.infotrygd.ef.rest.controller

import no.nav.infotrygd.ef.repository.PersonRepository
import no.nav.infotrygd.ef.repository.StønadRepository
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkResponse
import no.nav.infotrygd.ef.rest.api.StønadType
import no.nav.infotrygd.ef.testutil.TestData
import no.nav.infotrygd.ef.testutil.restClient
import no.nav.infotrygd.ef.testutil.restClientNoAuth
import org.assertj.core.api.Assertions.assertThat
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

    private val personsøkPath = "/infotrygd/enslig-forsoerger/personsok"
    private val løpendeSakPath = "/infotrygd/enslig-forsoerger/lopendeSak"

    @Test
    fun `infotrygd historikk søk`() {

        val person = TestData.person()
        val ukjentPerson = TestData.person()

        personRepository.saveAndFlush(person)

        val requestMedPersonSomFinnes = InfotrygdSøkRequest(listOf(person.fnr), StønadType.OVERGANGSSTØNAD)
        val requestMedUkjentPerson = InfotrygdSøkRequest(listOf(ukjentPerson.fnr), StønadType.OVERGANGSSTØNAD)

        val client = restClient(port)

        val res1 = kallStønadController(personsøkPath, client, requestMedPersonSomFinnes).responseBody()
        val res2 = kallStønadController(personsøkPath, client, requestMedUkjentPerson).responseBody()

        assertThat(res1.ingenTreff).isFalse()
        assertThat(res2.ingenTreff).isTrue()
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

        val res1 = kallStønadController(løpendeSakPath, client, requestMedPersonMedLøpendeSak).responseBody()
        val res2 = kallStønadController(løpendeSakPath, client, requestMedUkjentPerson).responseBody()

        assertThat(res1.ingenTreff).isFalse()
        assertThat(res2.ingenTreff).isTrue()
    }

    @Test
    fun `request uten fnr skal kaste bad request`() {
        val client = restClient(port)
        assertThat(kallStønadController(personsøkPath, client).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(kallStønadController(løpendeSakPath, client).statusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun noAuth() {
        val client = restClientNoAuth(port)
        val result = kallStønadController(personsøkPath, client)
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun clientAuth() {
        val client = restClient(port, subject = "wrong")
        val result = kallStønadController(personsøkPath, client)
        assertThat(result.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
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
