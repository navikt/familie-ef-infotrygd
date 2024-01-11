package no.nav.familie.ef.infotrygd

import no.nav.familie.ef.infotrygd.testutils.restClientNoAuth
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableMockOAuth2Server
class MainTest {
    @LocalServerPort
    var port: Int = 0

    @Test
    fun contextLoads() {
    }

    @Test
    fun health() {
        val response =
            restClientNoAuth(port)
                .get()
                .uri("/internal/health")
                .exchangeToMono { Mono.just(it.mutate().build()) }
                .block()!!

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK)
    }
}
