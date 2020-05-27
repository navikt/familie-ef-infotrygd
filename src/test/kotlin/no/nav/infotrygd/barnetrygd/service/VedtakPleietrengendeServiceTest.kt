package no.nav.infotrygd.barnetrygd.service

import no.nav.infotrygd.barnetrygd.repository.PeriodeRepository
import no.nav.infotrygd.barnetrygd.repository.VedtakRepository
import no.nav.infotrygd.barnetrygd.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = [VedtakPleietrengendeService::class])
internal class VedtakPleietrengendeServiceTest {

    @MockBean
    lateinit var periodeRepository: PeriodeRepository

    @MockBean
    lateinit var vedtakRepository: VedtakRepository

    @Autowired
    lateinit var vedtakPleietrengendeService: VedtakPleietrengendeService

    @Test
    fun hentingFiltreringGruppering() {
        val soekerFnr = TestData.foedselsNr()
        val annenSoekerFnr = TestData.foedselsNr()
        val barnFnr = TestData.foedselsNr()

        val periode = TestData.periode().copy(
            fnr = soekerFnr,
            morFnr = barnFnr
        )
        val vedtak = TestData.vedtak(
            fnr = soekerFnr,
            stonad = TestData.stonad(TestData.stonadBs(fnrBarn = barnFnr))
        )

        val periodeAnnenSoeker = TestData.periode().copy(
            fnr = annenSoekerFnr,
            morFnr = barnFnr
        )

        Mockito.`when`(periodeRepository.findByBarnFnr(barnFnr)).thenReturn(listOf(periode, periodeAnnenSoeker))
        Mockito.`when`(vedtakRepository.findByBarnFnr(barnFnr)).thenReturn(listOf(vedtak))

        val result = vedtakPleietrengendeService.finnVedtakForPleietrengende(barnFnr, LocalDate.now(), null)
        assertThat(result).hasSameSizeAs(listOf(soekerFnr, annenSoekerFnr))

        val soekerResult = result.find { it.soekerFnr == soekerFnr.asString } !!
        assertThat(soekerResult.vedtak).hasSameSizeAs(listOf(periode, vedtak))

        val annenSoekerResult = result.find { it.soekerFnr == annenSoekerFnr.asString } !!
        assertThat(annenSoekerResult.vedtak).hasSameSizeAs(listOf(periodeAnnenSoeker))

        val empty = vedtakPleietrengendeService.finnVedtakForPleietrengende(barnFnr, LocalDate.now().minusYears(1), LocalDate.now().minusYears(1))
        assertThat(empty).isEmpty()
    }

    @Test
    fun annullerteVedtak() {
        val soekerFnr = TestData.foedselsNr()
        val barnFnr = TestData.foedselsNr()

        val dato = LocalDate.of(2020, 1, 1)
        val vedtak = TestData.vedtak(
            fnr = soekerFnr,
            datoStart = dato,
            datoOpphoer = dato,
            vedtakSpFaBsOpphoer = dato,
            stonad = TestData.stonad(TestData.stonadBs(fnrBarn = barnFnr)),
            delytelserEksermpler = listOf(TestData.delytelse().copy(
                tom = dato
            ))
        )
        Mockito.`when`(vedtakRepository.findByBarnFnr(barnFnr)).thenReturn(listOf(vedtak))

        val empty = vedtakPleietrengendeService.finnVedtakForPleietrengende(barnFnr, dato, dato)
        assertThat(empty).isEmpty()
    }
}