package no.nav.familie.ef.infotrygd.repository

import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
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
@EnableMockOAuth2Server
internal class SakRepositoryTest {

    @Autowired lateinit var sakRepository: SakRepository
    @Autowired lateinit var jdbcTemplate: JdbcTemplate

    @Before
    fun setUp() {
        jdbcTemplate.update(
            """
            INSERT INTO sa_sak_10 (s01_personkey,s05_saksblokk,s10_saksnr,s10_reg_dato,s10_mottattdato,s10_kapittelnr,s10_valg,
            s10_undervalg,s10_dublett_feil,s10_type,s10_innstilling,s10_resultat,s10_nivaa,s10_innstilldato,s10_vedtaksdato,
            s10_iverksattdato,s10_grunnbl_dato,s10_aarsakskode,s10_tellepunkt,s10_telletype,s10_telledato,s10_eval_kode,
            s10_eval_tir,s10_fremlegg,s10_innstilling2,s10_innstilldato2,s10_annen_instans,s10_behen_type,s10_behen_enhet,
            s10_reg_av_type,s10_reg_av_enhet,s10_diff_framlegg,s10_innstillt_av_type,s10_innstillt_av_enhet,s10_vedtatt_av_type,
            s10_vedtatt_av_enhet,s10_prio_tab,s10_aoe,s10_es_system,s10_es_gsak_oppdragsid,s10_knyttet_til_sak,s10_vedtakstype,
            s10_reell_enhet,s10_mod_endret,tk_nr,f_nr,opprettet,endret_i_kilde,kilde_is,region,id_sak,oppdatert,db_splitt) VALUES
             ('304170040312345','M','04','1012011','1012011','EF','BT','  ',' ','MS','  ','FI','TK ','0','1012010','0','1012010',
             '00','000',' ','0','    ',' ','000','  ','0',' ','TK ','0617','TK ','0617','000','   ','0000','NFE','0689','      ',
             '   ',' ','0','00',' ','0000',' ','3041','70040312345',
             TO_TIMESTAMP('14.01.2022 21.48.43,830464000','DD.MM.RRRR HH24.MI.SSXFF'),
             TO_TIMESTAMP('14.01.2022 21.48.43,788490000','DD.MM.RRRR HH24.MI.SSXFF'),'RG01','1','17529101',
             TO_TIMESTAMP('14.01.2022 21.48.43,830464000','DD.MM.RRRR HH24.MI.SSXFF'),'EF')"""
        )
    }

    @After
    fun tearDown() {
        listOf("sa_sak_10").forEach {
            jdbcTemplate.update("TRUNCATE TABLE $it")
        }
    }

    @Test
    fun hentSaker() {
        val resultat = sakRepository.finnSaker(setOf("03047012345"))
        assertThat(resultat).hasSize(1)
        assertThat(resultat[0].personIdent).isEqualTo("03047012345")
    }
}
