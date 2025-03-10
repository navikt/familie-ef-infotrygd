package no.nav.familie.ef.infotrygd.rest.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(0)
class AuthorizationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        when (acceptedClient()) {
            true -> filterChain.doFilter(request, response)
            false -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authenticated, but unauthorized application")
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI.substring(request.contextPath.length)
        return path.startsWith("/internal/") ||
            path.startsWith("/swagger-ui/") ||
            path == "/tables" ||
            path == "/tables2" ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/v2/api-docs")
    }

    private fun acceptedClient(): Boolean =
        try {
            val claims = SpringTokenValidationContextHolder().getTokenValidationContext().getClaims("azure")

            @Suppress("UNCHECKED_CAST")
            val accessAsApplication =
                (
                    claims.get("roles") as List<String>?
                        ?: emptyList()
                ).contains("access_as_application")
            val clientId = claims?.get("azp") as String?

            if (!accessAsApplication) {
                logger.error("Mangler noe i token accessAsApplication=$accessAsApplication clientId=$clientId")
            }
            accessAsApplication
        } catch (e: Exception) {
            logger.error("Feilet med å hente azp fra token")
            false
        }
}
