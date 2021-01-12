package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class SøkFlereStønaderRequest(val identer: Set<String>,
                                   val stønader: Set<StønadType>)