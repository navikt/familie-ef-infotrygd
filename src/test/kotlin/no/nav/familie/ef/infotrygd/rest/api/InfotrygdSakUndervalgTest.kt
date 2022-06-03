package no.nav.familie.ef.infotrygd.rest.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class InfotrygdSakUndervalgTest {

    @Test
    fun skalMappeVerdi() {
        assertThat(InfotrygdSakUndervalg.fraInfotrygdKode("AK")).isEqualTo(InfotrygdSakUndervalg.AK)
        assertThat(InfotrygdSakUndervalg.fraInfotrygdKode("  ")).isNull()
    }
}
