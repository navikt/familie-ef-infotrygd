package no.nav.familie.ef.infotrygd.rest.api

import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResultat.Companion.fraInfotrygdKode
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class InfotrygdSakResultatTest {
    @Test
    fun skalMappeVerdierFraResultat() {
        assertThat(fraInfotrygdKode("  ")).isEqualTo(InfotrygdSakResultat.ÅPEN_SAK)
        assertThat(fraInfotrygdKode("AN ")).isEqualTo(InfotrygdSakResultat.ANNULLERING)
    }
}
