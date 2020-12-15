package no.nav.infotrygd.ef.service

import no.nav.infotrygd.ef.repository.PersonRepository
import no.nav.infotrygd.ef.repository.StønadRepository
import no.nav.infotrygd.ef.rest.api.InfotrygdSøkRequest
import no.nav.infotrygd.ef.rest.api.StønadType
import no.nav.infotrygd.ef.testutil.TestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
internal class StønadServiceTest {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var stonadRepository: StønadRepository

    private lateinit var stønadService: StønadService

    @Before
    fun setup() {
        stønadService = StønadService(personRepository, stonadRepository)
    }

    @Test
    fun `finnes skal returnere true`() {
        val soeker = TestData.person()

        personRepository.saveAndFlush(soeker)

        val soekerResult = stønadService.finnes(InfotrygdSøkRequest(listOf(soeker.fnr), StønadType.OVERGANGSSTØNAD))

        assertThat(soekerResult).isTrue()
    }

    @Test
    fun `finnes skal returnere false`() {
        val soekerFnr = TestData.foedselsNr()

        val resultEmptyEmpty = stønadService.finnes(InfotrygdSøkRequest(listOf(soekerFnr), StønadType.OVERGANGSSTØNAD))
        val resultEmptyNull = stønadService.finnes(InfotrygdSøkRequest(listOf(soekerFnr), StønadType.OVERGANGSSTØNAD))

        assertThat(resultEmptyEmpty).isFalse()
        assertThat(resultEmptyNull).isFalse()
    }

    @Test
    fun `mottar stønad skal returnere true når en av personene har en løpende sak`() {
        val person = TestData.person()
        val person2 = TestData.person()
        val stønad = TestData.stønad(person2)
        val stønad2 = TestData.stønad(person, opphørtFom = "111111")

        personRepository.saveAll(listOf(person, person2))
        stonadRepository.saveAll(listOf(stønad, stønad2))

        val mottarStønad =
            stønadService.mottarStønad(InfotrygdSøkRequest(listOf(person.fnr, person2.fnr), StønadType.OVERGANGSSTØNAD))

        assertThat(mottarStønad).isTrue()
    }

    @Test
    fun `mottarStønad skal returnere false for opphørt stønad`() {
        val person = TestData.person()
        val person2 = TestData.person()
        val stønad = TestData.stønad(person, opphørtFom = "111111")
        val stønad2 = TestData.stønad(person2, opphørtFom = "111111")

        personRepository.saveAll(listOf(person, person2))
        stonadRepository.saveAll(listOf(stønad, stønad2))

        val case1 = stønadService.mottarStønad(InfotrygdSøkRequest(listOf(person.fnr), StønadType.OVERGANGSSTØNAD))
        val case2 = stønadService.mottarStønad(InfotrygdSøkRequest(listOf(), StønadType.OVERGANGSSTØNAD))
        assertThat(case1).isFalse()
        assertThat(case2).isFalse()
    }

    @Test
    fun `mottarStønad skal returnere false når region ikke matcher`() {
        val person = TestData.person()
        val stønad = TestData.stønad(person, opphørtFom = "000000", region = "A")

        personRepository.saveAndFlush(person)
        stonadRepository.saveAndFlush(stønad)

        val mottarStønad =
            stønadService.mottarStønad(InfotrygdSøkRequest(listOf(person.fnr), StønadType.OVERGANGSSTØNAD))
        assertThat(mottarStønad).isFalse()
    }
}