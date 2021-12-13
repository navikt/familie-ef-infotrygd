package no.nav.familie.ef.infotrygd.rest

import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.core.NestedExceptionUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@Suppress("unused")
@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    private val secureLogger = LoggerFactory.getLogger("secureLogger")

    private fun rootCause(throwable: Throwable): Throwable {
        return NestedExceptionUtils.getMostSpecificCause(throwable)
    }

    override fun handleExceptionInternal(ex: Exception,
                                         body: Any?,
                                         headers: HttpHeaders,
                                         status: HttpStatus,
                                         request: WebRequest): ResponseEntity<Any> {
        secureLogger.error("En feil har oppstått", ex)
        logger.error("En feil har oppstått - throwable=${rootCause(ex).javaClass.simpleName} status=${status.value()}")
        return super.handleExceptionInternal(ex, body, headers, status, request)
    }

    @ExceptionHandler(Throwable::class)
    fun handleThrowable(throwable: Throwable): ResponseEntity<ErrorResponse> {
        val responseStatus = throwable::class.annotations.find { it is ResponseStatus }?.let { it as ResponseStatus }
        if (responseStatus != null) {
            return håndtertResponseStatusFeil(throwable, responseStatus)
        }
        return uventetFeil(throwable)
    }

    private fun uventetFeil(throwable: Throwable): ResponseEntity<ErrorResponse> {
        val rootCause = rootCause(throwable)
        secureLogger.error("En feil har oppstått", throwable)
        logger.error("En feil har oppstått - throwable=${rootCause.javaClass.simpleName} ")
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse(throwable, rootCause))
    }

    // Denne håndterer eks JwtTokenUnauthorizedException
    private fun håndtertResponseStatusFeil(throwable: Throwable,
                                           responseStatus: ResponseStatus): ResponseEntity<ErrorResponse> {
        val rootCause = rootCause(throwable)
        val status = if (responseStatus.value != HttpStatus.INTERNAL_SERVER_ERROR) responseStatus.value else responseStatus.code
        val loggMelding = "En håndtert feil har oppstått" +
                " throwable=${rootCause.javaClass.simpleName}" +
                " reason=${responseStatus.reason}" +
                " status=$status"

        loggFeil(throwable, loggMelding)
        return ResponseEntity.status(status).body(errorResponse(throwable, rootCause))
    }

    private fun errorResponse(throwable: Throwable, rootCause: Throwable) =
            ErrorResponse("Uventet feil ${rootCause.javaClass.simpleName}" +
                    " - ${throwable.message} - ${rootCause.message})")

    private fun loggFeil(throwable: Throwable, loggMelding: String) {
        when (throwable) {
            is JwtTokenUnauthorizedException -> logger.debug(loggMelding)
            else -> logger.error(loggMelding)
        }
    }

    data class ErrorResponse(val errorMessage: String)

}
