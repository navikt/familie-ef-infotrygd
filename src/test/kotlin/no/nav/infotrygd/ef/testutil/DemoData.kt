package no.nav.infotrygd.ef.testutil

import no.nav.infotrygd.ef.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("demoData")
class DemoData(
    private val personRepository: PersonRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        val person = TestData.person()

        personRepository.saveAndFlush(person)

        logger.info("Demo fnr.: ${person.fnr.asString}")
    }
}