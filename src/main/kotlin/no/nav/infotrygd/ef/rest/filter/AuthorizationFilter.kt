package no.nav.infotrygd.ef.rest.filter

import no.nav.security.token.support.spring.SpringTokenValidationContextHolder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(0)
class AuthorizationFilter(@Value("\${app.security.clientWhitelist}") private val clientWhitelistStr: String) :
        OncePerRequestFilter() {

    private val acceptedClients = clientWhitelistStr.split(',').map(String::trim).toSet()

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
        when (acceptedClient()) {
            true -> filterChain.doFilter(request, response)
            false -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authenticated, but unauthorized application")
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI.substring(request.contextPath.length)
        return path.startsWith("/internal/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/v2/api-docs")
    }

    private fun acceptedClient(): Boolean {
        return try {
            val claims = SpringTokenValidationContextHolder().tokenValidationContext.getClaims("azure")

            @Suppress("UNCHECKED_CAST")
            val accessAsApplication = (claims.get("roles") as List<String>?
                    ?: emptyList()).contains("access_as_application")
            val clientId = claims?.get("azp") as String?

            val result = accessAsApplication && clientId != null && acceptedClients.contains(clientId)
            if (!result) {
                logger.error("Mangler noe i token accessAsApplication=$accessAsApplication clientId=$clientId")
            }
            result
        } catch (e: Exception) {
            logger.error("Feilet med å hente azp fra token")
            false
        }
    }

}
