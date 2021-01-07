package no.nav.infotrygd.ef.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr
import java.time.LocalDate

data class PeriodeOvergangsstønadRequest(val identer: Set<FoedselsNr>,
                                         val fomDato: LocalDate?,
                                         val tomDato: LocalDate?)

data class PeriodeOvergangsstønadResponse(val perioder: List<PeriodeOvergangsstønad>)

data class PeriodeOvergangsstønad(val ident: FoedselsNr,
                                  val fomDato: LocalDate,
                                  val tomDato: LocalDate,
                                  val beløp: Float)