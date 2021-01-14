package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class SøkFlereStønaderRequest(val personIdenter: Set<String>,
                                   val stønader: Set<StønadType>)