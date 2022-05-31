package no.nav.familie.ef.infotrygd.testutil

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import no.nav.security.token.support.test.JwkGenerator
import no.nav.security.token.support.test.JwtTokenGenerator
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

fun restClient(port: Int, clientId: String? = "CLIENT_ID_EF_SAK", accessAsApplication: Boolean = true): WebClient {
    val createSignedJWT = token(clientId, accessAsApplication)

    return WebClient.builder()
        .baseUrl(baseUrl(port))
        .defaultHeader("Authorization", "Bearer ${createSignedJWT.serialize()}")
        .build()
}

fun restClientNoAuth(port: Int): WebClient {
    return WebClient.builder()
        .baseUrl("http://localhost:$port")
        .build()
}

private fun token(clientId: String?, accessAsApplication: Boolean): SignedJWT {
    val thisId = UUID.randomUUID().toString()
    var claimsSet = JwtTokenGenerator.createSignedJWT(clientId).jwtClaimsSet
    val builder = JWTClaimsSet.Builder(claimsSet)
        .claim("oid", thisId)
        .claim("sub", thisId)
        .claim("azp", clientId)

    if (accessAsApplication) {
        builder.claim("roles", listOf("access_as_application"))
    }

    claimsSet = builder.build()
    val createSignedJWT = JwtTokenGenerator.createSignedJWT(JwkGenerator.getDefaultRSAKey(), claimsSet)
    return createSignedJWT
}

private fun baseUrl(port: Int) = "http://localhost:$port"
