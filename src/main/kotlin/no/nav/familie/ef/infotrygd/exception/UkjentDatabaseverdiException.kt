package no.nav.familie.ef.infotrygd.exception

import java.lang.RuntimeException

class UkjentDatabaseverdiException(
    val verdi: String,
    gyldigeVerdier: List<String>,
) : RuntimeException("Ukjent databaseverdi '$verdi'. Tillatte verdier er: ${gyldigeVerdier.joinToString()}")
