package no.nav.infotrygd.ef.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr
import java.time.LocalDate

data class PeriodeArenaRequest(val personIdenter: Set<FoedselsNr>,
                               val fomDato: LocalDate?,
                               val tomDato: LocalDate?)

data class PeriodeResponse(val perioder: List<Periode>)

data class Periode(val personIdent: FoedselsNr,
                   val fomDato: LocalDate,
                   val tomDato: LocalDate,
                   val opphørsdato: LocalDate?,
                   val beløp: Float)