package no.nav.familie.ef.infotrygd.repository

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.familie.ef.infotrygd.integration.TableIntegrator
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

/**
 * Alle tabeller og kolonner som er brukt må være definiert slik att infotrygd-migreringsteamet hvet om hvilke tabeller som er i bruk
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableMockOAuth2Server
class RepoTest {

    @Autowired
    private lateinit var tableIntegrator: TableIntegrator

    private val jdbcTemplate = mockk<NamedParameterJdbcTemplate>()
    private val periodeRepository = PeriodeRepository(jdbcTemplate)
    private val infotrygdRepository = InfotrygdRepository(jdbcTemplate)
    private val sakRepository = SakRepository(jdbcTemplate)

    private val checkedTables = mutableSetOf<String>()
    private lateinit var hibernateTables: Map<String, List<String>>
    private lateinit var definedTables: MutableMap<String, MutableSet<String>>

    lateinit var sqlSlot: CapturingSlot<String>

    @Before
    fun setUp() {
        hibernateTables = tableIntegrator.tables
        definedTables = hibernateTables.map { it.key to it.value.toMutableSet() }.toMap().toMutableMap()

        sqlSlot = slot()
        every { jdbcTemplate.query(capture(sqlSlot), any<SqlParameterSource>(), any<RowMapper<*>>()) } returns emptyList()
    }

    @Test(expected = Throwable::class)
    fun `Når man kun sjekker en spørring feler testen`() {
        verifiserAttAlleKolonnerFinnesOgErBrukt(listOf({ periodeRepository.hentPerioderForArena(mockk(relaxed = true)) }))
    }

    @Test
    fun `Skal verifisere att alle tabeller or kolonner er brukt slik att migreringsteamet har peiling på hvilke tabeller som brukes`() {
        verifiserAttAlleKolonnerFinnesOgErBrukt(kombinasjonerAvQueries())
    }

    private fun verifiserAttAlleKolonnerFinnesOgErBrukt(queries: List<() -> Any>) {
        queries.forEach {
            try {
                it()
            } catch (e: Exception) {
                println(e.message)
            }
            verifyColumnsExists(sqlSlot.captured)
        }
        assertThat(checkedTables).containsExactlyInAnyOrderElementsOf(definedTables.keys)
        definedTables.forEach { (k, v) ->
            assertThat(v)
                .withFailMessage("$k har verdier som ikke trengs: $v")
                .isEmpty()
        }
    }

    private fun kombinasjonerAvQueries() = listOf(
        { periodeRepository.hentPerioderForArena(mockk(relaxed = true)) },
        { periodeRepository.hentPerioder(mockk(relaxed = true)) },
        { sakRepository.finnesSaker(emptySet()) },
        { sakRepository.finnSaker(emptySet()) },
        { infotrygdRepository.harStønad(emptySet(), true) },
        { infotrygdRepository.harStønad(emptySet(), false) }
    )

    private fun verifyColumnsExists(s: String) {
        val tables = """(FROM|JOIN) (\w+) (\w+)""".toRegex().findAll(s).map {
            val (_, table, tableKeyword) = it.destructured
            tableKeyword.lowercase() to table.lowercase()
        }.toMap()

        val columns = """[, \(](\w+)\.(\w+)""".toRegex().findAll(s).map {
            val (tableKeyword, column) = it.destructured
            tableKeyword.lowercase() to column.lowercase()
        }.groupBy({ it.first }) { it.second }

        val columnsOnTables = columns.map { it.key to Pair(tables[it.key]!!, it.value.toSet()) }.toMap()

        columnsOnTables.forEach { (_, v) ->
            val table = v.first
            val hibernateColumns = hibernateTables[table] ?: error("Savner tabellen $table i hibernate")
            val tableColumns = v.second
            assertThat(hibernateColumns).containsAll(tableColumns)

            definedTables[table]!!.removeAll(tableColumns)
            checkedTables.add(table)
        }
    }
}
