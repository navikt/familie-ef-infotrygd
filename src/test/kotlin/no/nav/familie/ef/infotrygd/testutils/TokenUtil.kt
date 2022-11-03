package no.nav.familie.ef.infotrygd.testutils
/*
import no.nav.security.mock.oauth2.MockOAuth2Server
import java.util.UUID

object TokenUtil {

    fun clientToken(mockOAuth2Server: MockOAuth2Server, clientId: String, accessAsApplication: Boolean): String {
        val thisId = UUID.randomUUID().toString()

        val claims = mapOf(
            "oid" to thisId,
            "azp" to clientId,
            "roles" to if (accessAsApplication) listOf("access_as_application") else emptyList()
        )

        return mockOAuth2Server.issueToken(
            issuerId = "azuread",
            subject = thisId,
            audience = "aud-localhost",
            claims = claims
        ).serialize()
    }
}

 */
