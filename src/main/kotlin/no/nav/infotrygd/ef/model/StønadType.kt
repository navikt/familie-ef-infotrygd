package no.nav.infotrygd.ef.model

enum class StønadType(val kodeRutine: String) {
    OVERGANGSSTØNAD("EO"),
    BARNETILSYN("EB"),
    SKOLEPENGER("EU");

    companion object {
        private val kodeRutiner = values().map { it.kodeRutine to it }.toMap()
        fun fraKodeRutine(kodeRutine: String): StønadType =
                kodeRutiner[kodeRutine] ?: error("Fant ikke StønadType fra kodeRutine=$kodeRutine")
    }
}