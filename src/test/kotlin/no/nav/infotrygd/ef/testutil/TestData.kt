package no.nav.infotrygd.ef.testutil

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import no.nav.infotrygd.ef.model.*
import no.nav.infotrygd.ef.nextId
import java.time.LocalDate

object TestData {
    fun foedselsNr(
        foedselsdato: LocalDate? = null,
        kjoenn: Kjoenn = Kjoenn.MANN): FoedselsNr {

        return fnrGenerator.foedselsnummer(
            foedselsdato = foedselsdato,
            kjoenn = kjoenn
        )
    }

    fun person(
        fnr: FoedselsNr = foedselsNr(),
        tkNr: String = "1000",
        personKey: Long = tkNr.let { it + fnr.asString }.toLong(),
        region: String = "X"
    ): Person {
        return Person(
            id = nextId(),
            fnr = fnr,
            tkNr = tkNr,
            personKey = personKey,
            region = region
        )
    }

    fun stønad(
        mottaker: Person,
        opphørtFom: String = "000000",
        region: String = "X"
    ): Stønad {
        return Stønad(
            id = nextId(),
            personKey = mottaker.personKey,
            fnr = mottaker.fnr,
            tkNr = mottaker.tkNr,
            opphørtFom = opphørtFom,
            region = region
        )
    }

    private val fnrGenerator = FoedselsnummerGenerator()
}