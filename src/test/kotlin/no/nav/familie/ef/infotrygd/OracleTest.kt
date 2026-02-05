package no.nav.familie.ef.infotrygd

import org.junit.Rule
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.containers.OracleContainer

@Disabled
class OracleTest {
    @get:Rule
    val oracle =
        OracleContainer("navoracle:12")
            .withStartupTimeoutSeconds(15)
            .withConnectTimeoutSeconds(1)

    @Test
    fun test() {
    }
}
