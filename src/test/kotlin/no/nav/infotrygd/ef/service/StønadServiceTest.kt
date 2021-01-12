package no.nav.infotrygd.ef.service

import no.nav.infotrygd.ef.model.StønadType
import no.nav.infotrygd.ef.rest.api.SøkFlereStønaderRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class StønadServiceTest {

    @Autowired lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var stønadService: StønadService

    @Before
    fun setup() {
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (1, '${IDENT}')")
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (2, '${IDENT2}')")
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (3, '${IDENT3}')")

        // Legger inn enn opphør bak i tid
        jdbcTemplate.update(
                """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, KODE_RUTINE, DATO_START, DATO_OPPHOR)
                                     VALUES (1, 1, 1, 'EO', sysdate, sysdate - 100)"""
        )

        // Legger inn enn opphør bak i tid
        jdbcTemplate.update(
                """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, KODE_RUTINE, DATO_START, DATO_OPPHOR)
                                     VALUES (2, 2, 2, 'EO', sysdate, null)"""
        )

        // Legger inn enn opphør bak i tid og med nulldato
        jdbcTemplate.update(
                """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, KODE_RUTINE, DATO_START, DATO_OPPHOR)
                                     VALUES (3, 3, 3, 'EO', sysdate, sysdate - 100)"""
        )
        jdbcTemplate.update(
                """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, KODE_RUTINE, DATO_START, DATO_OPPHOR)
                                     VALUES (3, 3, 3, 'EO', sysdate, null)"""
        )
    }

    @After
    fun tearDown() {
        listOf("T_LOPENR_FNR", "T_STONAD").forEach { jdbcTemplate.update("TRUNCATE TABLE $it") }
    }

    @Test
    fun `opphør bak i tid - har stønad men har ikke aktiv stønad`() {
        val finnes = stønadService.eksistererStønad(SøkFlereStønaderRequest(setOf(IDENT), setOf(StønadType.OVERGANGSSTØNAD)))

        assertThat(finnes.keys).containsOnly(StønadType.OVERGANGSSTØNAD)

        val stønadTreff = finnes[StønadType.OVERGANGSSTØNAD]!!
        assertThat(stønadTreff.eksisterer).isTrue
        assertThat(stønadTreff.harAktivStønad).isFalse
    }

    @Test
    fun `opphør er null - har stønad og har aktiv stønad`() {
        val finnes = stønadService.eksistererStønad(SøkFlereStønaderRequest(setOf(IDENT2), setOf(StønadType.OVERGANGSSTØNAD)))

        assertThat(finnes.keys).containsOnly(StønadType.OVERGANGSSTØNAD)

        val stønadTreff = finnes[StønadType.OVERGANGSSTØNAD]!!
        assertThat(stønadTreff.eksisterer).isTrue
        assertThat(stønadTreff.harAktivStønad).isTrue
    }

    @Test
    fun `har opphør bak i tid og opphør som er null - har stønad og har aktiv stønad`() {
        val finnes = stønadService.eksistererStønad(SøkFlereStønaderRequest(setOf(IDENT3), setOf(StønadType.OVERGANGSSTØNAD)))

        assertThat(finnes.keys).containsOnly(StønadType.OVERGANGSSTØNAD)

        val stønadTreff = finnes[StønadType.OVERGANGSSTØNAD]!!
        assertThat(stønadTreff.eksisterer).isTrue
        assertThat(stønadTreff.harAktivStønad).isTrue
    }

    @Test
    fun `har ikke noen stønader for disse typene men retunrerer likevel treff i resultatet`() {
        val finnes = stønadService.eksistererStønad(SøkFlereStønaderRequest(setOf(IDENT, IDENT2, IDENT3),
                                                                            setOf(StønadType.BARNETILSYN, StønadType.SKOLEPENGER)))

        assertThat(finnes.keys).containsExactlyInAnyOrder(StønadType.BARNETILSYN, StønadType.SKOLEPENGER)

        finnes.values.forEach {
            assertThat(it.eksisterer).isFalse
            assertThat(it.harAktivStønad).isFalse
        }
    }

    companion object {

        const val IDENT = "01234567890"
        const val IDENT2 = "01234567891"
        const val IDENT3 = "01234567892"
    }

}