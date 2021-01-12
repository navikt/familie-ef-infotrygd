package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class EksistererStønadResponse(val stønader: Map<StønadType, StønadTreff>)

data class StønadTreff(val eksisterer: Boolean,
                       val harAktivStønad: Boolean)