package no.nav.infotrygd.ef.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.ef.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long> {

    @Query("""
        SELECT p FROM Person p
         WHERE p.fnr IN :fnrList
    """)
    fun findByFnrList(fnrList: List<FoedselsNr>): List<Person>

}