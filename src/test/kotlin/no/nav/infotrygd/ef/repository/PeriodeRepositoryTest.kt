package no.nav.infotrygd.ef.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.ef.rest.api.PeriodeOvergangsstønad
import no.nav.infotrygd.ef.rest.api.PeriodeOvergangsstønadRequest
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
import java.time.LocalDate

//TODO hvordan skal vi håndtere når TOM datoer er null ? Den er nullable...

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class PeriodeRepositoryTest {

    @Autowired lateinit var periodeRepository: PeriodeRepository
    @Autowired lateinit var jdbcTemplate: JdbcTemplate

    @Before
    fun setUp() {
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (1, '01234567890')")
        jdbcTemplate.update(
            """INSERT INTO T_VEDTAK (VEDTAK_ID, PERSON_LOPENR, STONAD_ID, KODE_RUTINE, DATO_INNV_FOM,
                     DATO_INNV_TOM) VALUES (1,1,1,'EO',?,?)""",
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2022, 1, 1)
        )
        jdbcTemplate.update(
            """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, DATO_START, DATO_OPPHOR)
                                     VALUES (1, 1, 1, sysdate, sysdate)"""
        )
        jdbcTemplate.update("INSERT INTO T_DELYTELSE (VEDTAK_ID, TYPE_SATS, BELOP) VALUES (1, '', 100.34)")
        jdbcTemplate.update("INSERT INTO T_ENDRING (VEDTAK_ID, KODE) VALUES (1, 'A')")
        jdbcTemplate.update("INSERT INTO T_STONADSKLASSE (VEDTAK_ID, KODE_NIVAA, KODE_KLASSE) VALUES (1, '01', 'EF')")
        jdbcTemplate.update("INSERT INTO T_STONADSKLASSE (VEDTAK_ID, KODE_NIVAA, KODE_KLASSE) VALUES (1, '02', 'OG')")
        jdbcTemplate.update("INSERT INTO T_STONADSKLASSE (VEDTAK_ID, KODE_NIVAA, KODE_KLASSE) VALUES (1, '03', 'NY')")
    }

    @After
    fun tearDown() {
        listOf("T_LOPENR_FNR", "T_VEDTAK", "T_STONAD", "T_DELYTELSE", "T_STONADSKLASSE").forEach {
            jdbcTemplate.update("TRUNCATE TABLE $it")
        }
    }

    @Test
    fun `uten datoer`() {
        assertThat(hentPerioder()).hasSize(1)
    }

    @Test
    fun `beløp er riktig`() {
        val perioder = hentPerioder()
        assertThat(perioder).hasSize(1)
        assertThat(perioder.first().beløp).isEqualTo(100.34f)
    }

    @Test
    fun `fom og tom datoer`() {
        assertThat(hentPerioder(LocalDate.of(1900, 1, 1), LocalDate.of(2021, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(1900, 1, 1), LocalDate.of(2030, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 6, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(2020, 6, 1), LocalDate.of(2030, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(1900, 1, 1), LocalDate.of(1901, 1, 1)))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioder(LocalDate.of(2030, 1, 1), LocalDate.of(2031, 1, 1)))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten tom dato`() {
        assertThat(hentPerioder(LocalDate.of(1900, 1, 1), null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(2020, 6, 1), null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(LocalDate.of(2030, 1, 1), null))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten fom dato`() {
        assertThat(hentPerioder(null, LocalDate.of(1901, 1, 1)))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioder(null, LocalDate.of(2020, 6, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioder(null, LocalDate.of(2031, 1, 1)))
            .withFailMessage("FOM dato > TOM dato(db)")
            .hasSize(1)
    }

    private fun hentPerioder(fomDato: LocalDate? = null, tomDato: LocalDate? = null): List<PeriodeOvergangsstønad> =
        periodeRepository.hentPerioderForOvergangsstønad(
                PeriodeOvergangsstønadRequest(
                identer = setOf(FoedselsNr("01234567890")),
                fomDato,
                tomDato
            )
        )
}